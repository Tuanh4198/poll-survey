package io.yody.yosurvey.survey.service.jobs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.service.AwsS3MediaProvider;
import io.yody.yosurvey.service.dto.StagedUploadInput;
import io.yody.yosurvey.service.dto.StagedUploadTarget;
import io.yody.yosurvey.service.enumaration.StagedUploadResource;
import io.yody.yosurvey.survey.domain.ExportEntity;
import io.yody.yosurvey.survey.domain.constant.ExportComponentConst;
import io.yody.yosurvey.survey.domain.enumeration.ExportStatusEnum;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.repository.ExportRepository;
import io.yody.yosurvey.survey.service.EmployeeSurveyQueryService;
import io.yody.yosurvey.survey.service.SurveySubmitQueryService;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveyCriteria;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveySubmitCriteria;
import io.yody.yosurvey.survey.service.dto.*;
import io.yody.yosurvey.survey.service.helpers.DateHelper;
import io.yody.yosurvey.survey.service.helpers.ExcelHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


@Service
@Profile("worker")
public class ExportHandler {
    private static final Logger log = LoggerFactory.getLogger(ExportHandler.class);
    private final ExportRepository exportRepository;
    private final ObjectMapper objectMapper;
    private final EmployeeSurveyQueryService employeeSurveyQueryService;
    private final SurveySubmitQueryService surveySubmitQueryService;
    private final AwsS3MediaProvider awsS3MediaProvider;

    public ExportHandler(ExportRepository exportRepository, ObjectMapper objectMapper,
                         EmployeeSurveyQueryService employeeSurveyQueryService,
                         SurveySubmitQueryService surveySubmitQueryService,
                         AwsS3MediaProvider awsS3MediaProvider) {
        this.exportRepository = exportRepository;
        this.objectMapper = objectMapper;
        this.employeeSurveyQueryService = employeeSurveyQueryService;
        this.surveySubmitQueryService = surveySubmitQueryService;
        this.awsS3MediaProvider = awsS3MediaProvider;
    }


    @Scheduled(cron = "*/10 * * * * *")
    public void doHandle() {
        scanForExports();
    }
    @Transactional
    public void scanForExports() {
        if (exportRepository.existsByStatus(ExportStatusEnum.PENDING)) {
            List<ExportEntity> pendingExports = exportRepository.findAllByStatus(ExportStatusEnum.PENDING);
            for (ExportEntity export : pendingExports) {
                export.setStatus(ExportStatusEnum.PROCESSING);
            }
            exportRepository.saveAll(pendingExports);
            for (ExportEntity export : pendingExports) {
                try {
                    handleExport(export);
                } catch (Exception e) {
                    log.error("error export file {}", e.getMessage());
                    export.setStatus(ExportStatusEnum.ERROR);
                    export.setReason(e.getMessage());
                    try {
                        exportRepository.save(export);
                    } catch (Exception ex) {
                        log.error("error save error {}", ex.getMessage());
                    }
                }
            }
        } else {
            System.out.println("No pending exports to process.");
        }
    }

    private void handleExport(ExportEntity exportEntity) throws IOException {
        if (exportEntity.getComponent().equals(ExportComponentConst.EMPLOYEE_SURVEY)) {
            ExcelDataStrategy<EmployeeSurveyCriteria, EmployeeSurveyDTO>
                strategy = new EmployeeSurveyDataStrategy(employeeSurveyQueryService);
            exportData(exportEntity, strategy);
        } else if (exportEntity.getComponent().equals(ExportComponentConst.EMPLOYEE_SURVEY_SUBMIT)) {
            ExcelDataStrategy<EmployeeSurveySubmitCriteria, SurveySubmitDTO>
                strategy = new EmployeeSurveySubmitDataStrategy(surveySubmitQueryService);
            exportData(exportEntity, strategy);
        } else {
            throw new IllegalArgumentException("Invalid export component: " + exportEntity.getComponent());
        }
    }

    private <CriteriaType, DTO> void exportData(
        ExportEntity exportEntity, ExcelDataStrategy<CriteriaType, DTO> strategy) throws IOException {
        CriteriaType criteria = strategy.initializeCriteria(exportEntity.getConditions(), objectMapper);

        if (!strategy.isCriteriaValid(criteria)) {
            exportEntity.setStatus(ExportStatusEnum.ERROR);
            exportEntity.setReason("Survey id is not exist");
            exportRepository.save(exportEntity);
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        HashMap<String, CellStyle> styles = ExcelHelper.getCellStylePatterns(workbook);
        Sheet worksheet = workbook.createSheet();
        ExcelHelper.writeTemplateSheet(strategy.getHeaders(), worksheet);

        processExportData(criteria, worksheet, styles, strategy, exportEntity);

        String fileName = String.format("%s_%s.xlsx", exportEntity.getChecksum(), DateHelper.formatDate(new Date(), "YYYYMMDD"));
        String filePath = ExportComponentConst.TEMP_PATH + fileName;
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            workbook.write(fileOutputStream);
        }
        log.info("Export completed: {}", filePath);
        handleUpload(exportEntity, fileName);
    }

    private <CriteriaType, DTO> void processExportData(CriteriaType criteria, Sheet worksheet, HashMap<String, CellStyle> styles,
                                                  ExcelDataStrategy<CriteriaType, DTO> strategy, ExportEntity exportEntity) {
        int pageNumber = 1;
        int row = 1;
        int totalRecordsProcessed = 0;
        int batchCounter = 0;
        final int updateAfterBatchCount = 3;

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber - 1, strategy.getPageSize());
            Page<DTO> page = strategy.fetchPageData(criteria, pageable);
            List<ExcelDTO> pageData = strategy.convertPageData(page.getContent());

            for (ExcelDTO dto : pageData) {
                dto.setIndex(row);
                ExcelHelper.writeObject(dto, strategy.getColNum(), row, worksheet, styles);
                row++;
            }


            totalRecordsProcessed += page.getNumberOfElements();
            log.info("Total records processed: {}", totalRecordsProcessed);

            if (!page.hasNext() || totalRecordsProcessed >= strategy.getMaxRecord()) {
                exportEntity.setTotal(page.getTotalElements());
                exportEntity.setCurrent((long) totalRecordsProcessed);
                exportEntity.setStatus(ExportStatusEnum.PROCESSING);
                exportRepository.save(exportEntity);
                break;
            }

            pageNumber++;
            batchCounter++;

            if (batchCounter >= updateAfterBatchCount) {
                batchCounter = 0;
                exportEntity.setTotal(page.getTotalElements());
                exportEntity.setCurrent((long) totalRecordsProcessed);
                exportEntity.setStatus(ExportStatusEnum.PROCESSING);
                exportRepository.save(exportEntity);
            }
        }

        ExcelHelper.formatTemplate(strategy.getHeaders(), worksheet);
    }

    private void handleUpload(ExportEntity exportEntity, String fileName) {
        log.info("start upload to s3 {}", fileName);
        StagedUploadInput uploadInput = new StagedUploadInput();
        uploadInput.setFilename(fileName);
        uploadInput.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        uploadInput.setResource(StagedUploadResource.FILE);
        List<StagedUploadTarget> stagedUploadTargets = awsS3MediaProvider.createStagedUploads(List.of(uploadInput));
        StagedUploadTarget stagedUploadTarget = stagedUploadTargets.stream().findFirst().orElse(null);
        if (stagedUploadTarget != null) {
            String presignedUrl = stagedUploadTarget.getResourceUrl();
            String url = stagedUploadTarget.getUrl();
            // Upload the file to the presigned URL
            File fileToUpload = new File(ExportComponentConst.TEMP_PATH + fileName);
            if (uploadFileToPresignedUrl(presignedUrl, fileToUpload)) {
                String finalResourceUrl = awsS3MediaProvider.createPreSignedGetUrl(url);
                exportEntity.setUrl(finalResourceUrl);
                exportEntity.setStatus(ExportStatusEnum.FINISH);
            } else {
                exportEntity.setUrl(presignedUrl);
                exportEntity.setStatus(ExportStatusEnum.ERROR);
                exportEntity.setReason("Error while uploading to S3 " + fileName);
            }
        } else {
            exportEntity.setStatus(ExportStatusEnum.ERROR);
            exportEntity.setReason("Error generating presigned URL");
        }
        exportRepository.save(exportEntity);
    }
    private boolean uploadFileToPresignedUrl(String presignedUrl, File file) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(presignedUrl);

            // Set the file entity with the correct content type for Excel files
            FileEntity fileEntity = new FileEntity(file);
            fileEntity.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            httpPut.setEntity(fileEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                return statusCode >= 200 && statusCode < 300;
            }
        } catch (IOException e) {
            log.error("export upload to presign url s3 error {}", e.getMessage());
            return false;
        }
    }

}

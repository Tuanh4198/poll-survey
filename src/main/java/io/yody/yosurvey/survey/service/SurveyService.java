package io.yody.yosurvey.survey.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.survey.client.PegasusClient;
import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.MetafieldEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.domain.constant.MetafieldConstant;
import io.yody.yosurvey.survey.domain.enumeration.*;
import io.yody.yosurvey.survey.repository.EmployeeSurveyRepository;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.business.AssignStrategyBO;
import io.yody.yosurvey.survey.service.business.AssignStrategyMetafieldBO;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.cache.EmployeeCache;
import io.yody.yosurvey.survey.service.dto.*;
import io.yody.yosurvey.survey.service.helpers.*;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.yody.yosurvey.survey.web.rest.request.*;
import io.yody.yosurvey.survey.web.rest.response.EmployeeDTO;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.errors.NtsValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service Implementation for managing {@link SurveyEntity}.
 */
@Service
@Transactional
public class SurveyService {
    private final Logger log = LoggerFactory.getLogger(SurveyService.class);
    private static final int BATCH_SIZE = 100;
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final IdHelper idHelper;
    private final SurveyTransactionManager transactionManager;
    private final MetafieldHelper metafieldHelper;
    private final PegasusClient pegasusClient;
    private final EmployeeSurveyRepository employeeSurveyRepository;
    private final EmployeeCache employeeCache;
    private final ThumbHelper thumbHelper;
    private final SurveySubmitRepository surveySubmitRepository;
    public SurveyService(SurveyRepository surveyRepository, SurveyMapper surveyMapper,
                         IdHelper idHelper, SurveyTransactionManager transactionManager,
                         MetafieldHelper metafieldHelper, PegasusClient pegasusClient,
                         EmployeeSurveyRepository employeeSurveyRepository, EmployeeCache employeeCache,
                         ThumbHelper thumbHelper, SurveySubmitRepository surveySubmitRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.idHelper = idHelper;
        this.transactionManager = transactionManager;
        this.metafieldHelper = metafieldHelper;
        this.pegasusClient = pegasusClient;
        this.employeeSurveyRepository = employeeSurveyRepository;
        this.employeeCache = employeeCache;
        this.thumbHelper = thumbHelper;
        this.surveySubmitRepository = surveySubmitRepository;
    }
    private void validateRequest(SurveyRequest request) {

    }
    private void processTempId(SurveyRequest request) {
        idHelper.processSurveyId(List.of(request));
    }
    private void processImport(List<AssignStrategyRequest> requests) throws IOException {
        for (AssignStrategyRequest request : requests) {
            List<MetafieldRequest> metafieldRequests = request.getMetafields();
            List<String> participantCodes = new ArrayList<>();
            if (!Objects.isNull(request.getFileParticipant())) {
                participantCodes = ImportFileHelper.parseGroupUsersFromFile(request.getFileParticipant().getBase64());
            }
            if (!CollectionUtils.isEmpty(participantCodes)) {
                for (MetafieldRequest metafieldRequest : metafieldRequests) {
                    if (!AssignStrategyMetafieldEnum.inValidKey(metafieldRequest.getKey())) {
                            if (metafieldRequest.getKey().equals(AssignStrategyMetafieldEnum.PARTICIPANTS.getKey())) {
                            if (metafieldRequest.getType().equals(ParticipantEmployeeTypeEnum.SPEC_USERS.getKey())) {
                                metafieldRequest.setValue(String.join("||", participantCodes));
                            }
                        }
                    }
                };
            }
            List<String> targetCodes = new ArrayList<>();
            if (!Objects.isNull(request.getFileTargets())) {
                targetCodes = ImportFileHelper.parseGroupUsersFromFile(request.getFileTargets().getBase64());
            }
            if (!CollectionUtils.isEmpty(targetCodes)) {
                for (MetafieldRequest metafieldRequest : metafieldRequests) {
                    if (!AssignStrategyMetafieldEnum.inValidKey(metafieldRequest.getKey())) {
                        if (metafieldRequest.getKey().equals(AssignStrategyMetafieldEnum.TARGETS.getKey())) {
                            if (metafieldRequest.getType().equals(TargetTypeEnum.SPEC_USERS.getKey())) {
                                metafieldRequest.setValue(String.join("||", targetCodes));
                            }
                        }
                    }
                };
            }
        }
    }
    /**
     * Save a survey.
     *
     * @param surveyDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyDTO save(SurveyRequest request) throws IOException {
        log.debug("Request to save Survey : {}", request);
        validateRequest(request);
        processTempId(request);
        processImport(request.getAssignStrategies());
        SurveyAggregate surveyAggregate = new SurveyAggregate(request);
        surveyAggregate.validate();
        SurveyDTO surveyDTO = transactionManager.save(surveyAggregate);
        return surveyDTO;
    }

    /**
     * Update a survey.
     *
     * @param surveyDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyDTO update(SurveyRequest request) throws IOException {
        log.debug("Request to update Survey : {}", request);
        validateRequest(request);
        processTempId(request);
        processImport(request.getAssignStrategies());
        SurveyAggregate aggregate = transactionManager.findById(request.getId());
        if (!Objects.isNull(aggregate)) {
            metafieldHelper.enrichMetafieldsSurveyAggregate(List.of(aggregate));
            metafieldHelper.enrichMetafieldsAssignStrategyBO(SurveyHelper.getAssignStrategyBos(aggregate));
            metafieldHelper.enrichMetafieldsBlockBO(SurveyHelper.getBlockBos(aggregate));
            metafieldHelper.enrichMetafieldsBlockFieldsBO(SurveyHelper.getBlockFieldBos(aggregate));
        }

        List<MetafieldEntity> oldMetafields = MetafieldHelper.getAllMetafields(aggregate);
        aggregate.update(request);
        aggregate.validate();

        SurveyDTO surveyDTO = transactionManager.update(oldMetafields, aggregate);
        return surveyDTO;
    }

    public void submit(String hash, SurveySubmitRequest request) {
        log.debug("Request to submit {}", request);
        SurveyEntity survey = surveyRepository.findByHash(hash);
        Instant applyTime = survey.getApplyTime();
        Instant endTime = survey.getEndTime();
        Instant now = Instant.now();
        if (applyTime.isAfter(now)) {
            throw new NtsValidationException("message", "Khảo sát chưa bắt đầu");
        }
        if (!Objects.isNull(endTime) && endTime.isBefore(now)) {
            throw new NtsValidationException("message", "Khảo sát đã kết thúc");
        }
        SurveyDTO surveyDTO = surveyMapper.toDto(survey);
        metafieldHelper.enrichMetafieldAssignStrategy(SurveyHelper.getAssignStrategyDtos(surveyDTO));
        metafieldHelper.enrichMetafieldBlocks(SurveyHelper.getBlockDtos(surveyDTO));
        metafieldHelper.enrichMetafieldBlockFields(SurveyHelper.getBlockFieldDtos(surveyDTO));

        List<FieldSubmitRequest> fieldSubmits = request.getFieldSubmits();
        List<BlockDTO> blocks = surveyDTO.getBlocks();
        Map<Long, Long> mapFieldIdToBlockId = new HashMap<>();
        Map<Long, BlockFieldsDTO> mapFieldIdToBlockFields = new HashMap<>();
        for (BlockDTO block : blocks) {
            List<BlockFieldsDTO> blockFields = block.getBlockFields();
            for (BlockFieldsDTO blockField : blockFields) {
                mapFieldIdToBlockId.put(blockField.getId(), block.getId());
                mapFieldIdToBlockFields.put(blockField.getId(), blockField);
            }
        }

        AssignStrategyDTO assignStrategyDTO = surveyDTO.getAssignStrategies().stream().findFirst().orElse(null);
        if (ObjectUtils.isEmpty(assignStrategyDTO)) {
            throw new NtsValidationException("message", "Không có đối tượng đánh giá");
        }
        MetafieldDTO targetDto = assignStrategyDTO.getMetafields().stream().filter(
            metafieldDTO -> metafieldDTO.getKey().equals(AssignStrategyMetafieldEnum.TARGETS.getKey())
        ).findFirst().orElse(null);
        if (ObjectUtils.isEmpty(targetDto)) {
            throw new NtsValidationException("message", "Không có đối tượng đánh giá");
        }
        String target = targetDto.getValue();
        List<SurveySubmitEntity> surveySubmits = new ArrayList<>();
        for (FieldSubmitRequest fieldSubmit : fieldSubmits) {
            Long fieldId = fieldSubmit.getFieldId();
            Long blockId = fieldSubmit.getBlockId();
            BlockFieldsDTO blockField = mapFieldIdToBlockFields.get(fieldId);
            if (!mapFieldIdToBlockId.get(fieldId).equals(blockId)) {
                throw new NtsValidationException("message",
                    String.format("Trường {} không thuộc block {}", fieldId, blockId));
            }
            Long surveyId = survey.getId();
            String fieldType = blockField.getType().getName();
            surveySubmits.add(
                new SurveySubmitEntity()
                    .code("")
                    .name("")
                    .target(target)
                    .targetName(target)
                    .surveyId(surveyId)
                    .blockId(blockId)
                    .type(fieldSubmit.getType())
                    .fieldId(fieldId)
                    .fieldName(blockField.getFieldName())
                    .fieldValue(fieldSubmit.getFieldValue())
                    .fieldType(fieldType)
            );
        }

        surveySubmitRepository.saveAll(surveySubmits);
    }

    /**
     * Partially update a survey.
     *
     * @param surveyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SurveyDTO> partialUpdate(SurveyDTO surveyDTO) {
        log.debug("Request to partially update Survey : {}", surveyDTO);

        return surveyRepository
            .findById(surveyDTO.getId())
            .map(existingSurvey -> {
                surveyMapper.partialUpdate(existingSurvey, surveyDTO);

                return existingSurvey;
            })
            .map(surveyRepository::save)
            .map(surveyMapper::toDto);
    }

    /**
     * Delete the survey by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Survey : {}", id);
        surveyRepository.deleteById(id);
    }

    private void mergeMaps(Map<String, List<String>> participantCodeMap, Map<String, List<String>> otherParticipantCodeMap) {
        for (Map.Entry<String, List<String>> entry : otherParticipantCodeMap.entrySet()) {
            String key = entry.getKey();
            List<String> newValues = entry.getValue();

            if (participantCodeMap.containsKey(key)) {
                // If the key exists, merge the lists
                participantCodeMap.get(key).addAll(newValues);
            } else {
                // If the key does not exist, put the new entry
                participantCodeMap.put(key, new ArrayList<>(newValues));
            }
        }
    }

    private void deletePendingSurvey(SurveyAggregate aggregate) {
        employeeSurveyRepository.deleteBySurveyIdAndStatus(aggregate.getId(), SurveyStatusEnum.PENDING);
    }
    @Transactional
    public void createSurvey(SurveyAggregate aggregate, Map<String, List<String>> participantCodeMap,
                              String participantType, String targetType, SurveyStatusEnum status) {
        Long surveyId = aggregate.getId();
        List<EmployeeSurveyEntity> surveysToSave = new ArrayList<>();
        List<EmployeeSurveyEntity> existingSurveys = employeeSurveyRepository.findBySurveyId(surveyId);

        // Create a map from the existing surveys grouped by target
        Map<String, List<EmployeeSurveyEntity>> existingSurveysByTarget = existingSurveys.stream()
            .collect(Collectors.groupingBy(EmployeeSurveyEntity::getTarget));

        // Iterate through the participantCodeMap to process each target
        for (Map.Entry<String, List<String>> entry : participantCodeMap.entrySet()) {
            String target = entry.getKey();
            List<String> newParticipantCodes = entry.getValue();

            // Get the existing surveys for the current target
            List<EmployeeSurveyEntity> surveysForTarget = existingSurveysByTarget.getOrDefault(target, new ArrayList<>());

            // Create a set of existing codes for easy lookup
            Set<String> existingCodes = surveysForTarget.stream()
                .map(EmployeeSurveyEntity::getCode)
                .collect(Collectors.toSet());

            // Create new surveys for codes not already existing
            for (String code : newParticipantCodes) {
                if (!existingCodes.contains(code) && !ObjectUtils.isEmpty(code)) {
                    String name = employeeCache.getNameByCode(code);
                    String targetName = target;
                    if (targetType.equals(TargetTypeEnum.SPEC_USERS.getKey())) {
                        targetName = employeeCache.getNameByCode(target);
                    }
                    EmployeeSurveyEntity newSurvey = new EmployeeSurveyEntity()
                        .surveyId(surveyId)
                        .code(code)
                        .name(name)
                        .participantType(participantType)
                        .target(target)
                        .targetType(targetType)
                        .targetName(targetName)
                        .status(status);

                    existingCodes.add(code);
                    //tránh trường hợp tự đánh giá bản thân
                    if (!(targetType.equals(TargetTypeEnum.SPEC_USERS.getKey())
                        && !participantType.equals(ParticipantEmployeeTypeEnum.SELF.getKey()) && code.equals(target))) {
                        surveysToSave.add(newSurvey);
                    }

                    // Save in batch of BATCH_SIZE
                    if (surveysToSave.size() >= BATCH_SIZE) {
                        surveysToSave = employeeSurveyRepository.saveAll(surveysToSave);
                        thumbHelper.enrichThumb(List.of(aggregate), SurveyAggregate::getThumbUrl, SurveyAggregate::setThumbUrl);
                        NotifyHelper.sendNotifyBySurvey(aggregate.getThumbUrl(), surveysToSave, pegasusClient::sendAssignSurvey, status);
                        surveysToSave.clear();
                    }
                }
            }
        }

        // Save any remaining surveys
        if (!surveysToSave.isEmpty()) {
            employeeSurveyRepository.saveAll(surveysToSave);
            NotifyHelper.sendNotifyBySurvey(aggregate.getThumbUrl(), surveysToSave, pegasusClient::sendAssignSurvey, status);
        }
    }
    private void assignTargetTypeUsers(SurveyAggregate aggregate, String targetValue, String participantType,
                                       String participantValue, SurveyStatusEnum status) {
        try {
            List<String> targetCodes = List.of(targetValue.split("\\|\\|"));
            Map<String, List<String>> participantCodeMap = new HashMap<>();
            if (participantType.equals(ParticipantEmployeeTypeEnum.SELF.getKey())) {
                for (String targetCode : targetCodes) {
                    participantCodeMap.put(targetCode, List.of(targetCode));
                }
            }
            if (participantType.equals(ParticipantEmployeeTypeEnum.SPEC_USERS.getKey())) {
                List<String> codes = List.of(participantValue.split("\\|\\|"));
                for (String targetCode : targetCodes) {
                    participantCodeMap.put(targetCode, codes);
                }
            }
            if (participantType.equals(ParticipantEmployeeTypeEnum.ANY_USERS.getKey())) {
                List<EmployeeDTO> employees = employeeCache.getAllEmployeesCache();
                List<String> codes = employees.stream().map(EmployeeDTO::getCode).collect(Collectors.toList());
                for (String targetCode : targetCodes) {
                    participantCodeMap.put(targetCode, codes);
                }
            }
            List<String> allSegments = List.of(ParticipantEmployeeTypeEnum.DEPARTMENT_MANAGER.getKey(),
                ParticipantEmployeeTypeEnum.EMPLOYEE_SAME_DEPARTMENT.getKey(),
                ParticipantEmployeeTypeEnum.EMPLOYEE_SAME_LEVEL_SAME_DEPARTMENT.getKey(),
                ParticipantEmployeeTypeEnum.EMPLOYEE_LOWER_LEVEL_SAME_DEPARTMENT.getKey());

            if (allSegments.contains(participantType)) {
                String participantCodeMapStr = pegasusClient.searchByEmployeeSegments(targetCodes, List.of(participantType));
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> otherParticipantCodeMap = objectMapper.readValue(participantCodeMapStr, Map.class);
                mergeMaps(participantCodeMap, otherParticipantCodeMap);
            }

            createSurvey(aggregate, participantCodeMap, participantType, TargetTypeEnum.SPEC_USERS.getKey(), status);
        } catch (JsonProcessingException e) {
            log.error("parse participant failed {}", participantType);
        }
    }
    private void assignTargetTypeDepartment(SurveyAggregate aggregate, String targetValue,
                                            String participantType, String participantValue,
                                            SurveyStatusEnum status) {
        try {
            Map<String, List<String>> participantCodeMap = new HashMap<>();
            String participantCodeMapStr = pegasusClient.searchByDepartmentSegments(targetValue, List.of(participantType));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<String>> otherParticipantCodeMap = objectMapper.readValue(participantCodeMapStr, Map.class);
            mergeMaps(participantCodeMap, otherParticipantCodeMap);

            createSurvey(aggregate, participantCodeMap, participantType, TargetTypeEnum.DEPARTMENT.getKey(), status);
        } catch (JsonProcessingException e) {
            log.error("parse participant failed {}", participantType);
        }
    }

    private void assignTargetTypeOther(SurveyAggregate aggregate, String targetValue,
                                       String participantType, String participantValue,
                                       SurveyStatusEnum status) {
        List<String> targets = List.of(targetValue.split("\\|\\|"));
        Map<String, List<String>> participantCodeMap = new HashMap<>();
        if (participantType.equals(ParticipantEmployeeTypeEnum.SPEC_USERS.getKey())) {
            List<String> codes = List.of(participantValue.split("\\|\\|"));
            for (String target : targets) {
                participantCodeMap.put(target, codes);
            }
        }
        createSurvey(aggregate, participantCodeMap, participantType, TargetTypeEnum.OTHER.getKey(), status);
    }

    private SurveyStatusEnum getStatus(SurveyAggregate aggregate) {
        SurveyStatusEnum status = SurveyStatusEnum.PENDING;
        Instant now = Instant.now();
        if (aggregate.getApplyTime().isBefore(now)) {
            status = SurveyStatusEnum.NOT_ATTENDED;
        };
        return status;
    }

    private void doAssign(SurveyAggregate aggregate, SurveyStatusEnum status) {
        List<AssignStrategyBO> assignStrategies = aggregate.getAssignStrategies();
        deletePendingSurvey(aggregate);
        for (AssignStrategyBO assignStrategyBO : assignStrategies) {
            List<AssignStrategyMetafieldBO> metafields = assignStrategyBO.getMetafields();
            if (ObjectUtils.isEmpty(metafields)) continue;
            AssignStrategyMetafieldBO targetMetafield = metafields.stream().filter(m ->
                m.getKey().equals(AssignStrategyMetafieldEnum.TARGETS.getKey())).findFirst().orElse(null);
            AssignStrategyMetafieldBO participantMetafield = metafields.stream().filter(m ->
                m.getKey().equals(AssignStrategyMetafieldEnum.PARTICIPANTS.getKey())).findFirst().orElse(null);
            if (!Objects.isNull(participantMetafield) && !Objects.isNull(targetMetafield)) {
                String participantType = participantMetafield.getType();
                String participantValue = participantMetafield.getValue();
                String targetType = targetMetafield.getType();
                String targetValue = targetMetafield.getValue();

                if (targetType.equals(TargetTypeEnum.SPEC_USERS.getKey())) {
                    assignTargetTypeUsers(aggregate, targetValue, participantType, participantValue, status);
                }
                if (targetType.equals(TargetTypeEnum.DEPARTMENT.getKey())) {
                    assignTargetTypeDepartment(aggregate, targetValue, participantType, participantValue, status);
                }
                if (targetType.equals(TargetTypeEnum.OTHER.getKey())) {
                    assignTargetTypeOther(aggregate, targetValue, participantType, participantValue, status);
                }
            }
        }
    }

    public void assign(Long surveyId) {
        SurveyAggregate aggregate = transactionManager.findById(surveyId);
        metafieldHelper.enrichMetafieldsAssignStrategyBO(SurveyHelper.getAssignStrategyBos(aggregate));

        SurveyStatusEnum status = getStatus(aggregate);
        doAssign(aggregate, status);
    }

    public String getHash(Long surveyId) {
        SurveyAggregate aggregate = transactionManager.findById(surveyId);
        String hash = aggregate.getHash();
        return hash;
    }

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "handleAssignSurvey", lockAtLeastFor = "1m")
    public void handleAssignSurvey() {
        Instant now = Instant.now();
        Instant from = now.minus(5, ChronoUnit.MINUTES);
        Instant to = now;
        List<SurveyEntity> surveys = surveyRepository.findByApplyTimeBetween(from, to);
        List<Long> surveyIds = surveys.stream().map(SurveyEntity::getId).collect(Collectors.toList());
        List<EmployeeSurveyEntity> pendingEmployeeSurveys = employeeSurveyRepository.findAllBySurveyIdInAndStatus(
            surveyIds, SurveyStatusEnum.PENDING
        );

        for (EmployeeSurveyEntity survey : pendingEmployeeSurveys) {
            survey.setStatus(SurveyStatusEnum.NOT_ATTENDED);
        }

        BatchHelper.saveInBatches(pendingEmployeeSurveys, employeeSurveyRepository, 100);

        for (SurveyEntity survey : surveys) {
            List<EmployeeSurveyEntity> currentEmployeeSurveys = pendingEmployeeSurveys.stream()
                .filter(employeeSurvey -> employeeSurvey.getSurveyId().equals(survey.getId()))
                .collect(Collectors.toList());
            // Send notification for the current survey
            NotifyHelper.sendNotifyBySurvey(survey.getThumbUrl(), currentEmployeeSurveys,
                pegasusClient::sendAssignSurvey, SurveyStatusEnum.NOT_ATTENDED);
        }

        for (SurveyEntity survey : surveys) {
            assign(survey.getId());
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    @SchedulerLock(name = "handleAboutToExpireSurvey", lockAtLeastFor = "1m")
    public void handleAboutToExpireSurvey() {
        Instant now = Instant.now();
        Instant from = now.plus(24, ChronoUnit.MINUTES);
        Instant to = now.plus(29, ChronoUnit.MINUTES);
        List<SurveyEntity> surveys = surveyRepository.findByEndTimeBetween(from, to);
        List<Long> surveyIds = surveys.stream().map(SurveyEntity::getId).collect(Collectors.toList());
        List<EmployeeSurveyEntity> employeeSurveys = employeeSurveyRepository
            .findAllBySurveyIdInAndStatus(surveyIds, SurveyStatusEnum.NOT_ATTENDED);

        for (SurveyEntity survey : surveys) {
            List<EmployeeSurveyEntity> currentEmployeeSurveys = employeeSurveys.stream()
                .filter(employeeSurvey -> employeeSurvey.getSurveyId().equals(survey.getId()))
                .collect(Collectors.toList());
            // Send notification for the current survey
            NotifyHelper.sendNotifyBySurvey(survey.getThumbUrl(), currentEmployeeSurveys, pegasusClient::sendAboutToExpireSurvey, SurveyStatusEnum.NOT_ATTENDED);
        }
    }
}

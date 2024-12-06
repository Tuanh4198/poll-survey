package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.ExportEntity;
import io.yody.yosurvey.survey.domain.constant.ExportComponentConst;
import io.yody.yosurvey.survey.repository.ExportRepository;
import io.yody.yosurvey.survey.service.dto.ExportDTO;
import io.yody.yosurvey.survey.service.mapper.ExportMapper;
import io.yody.yosurvey.survey.web.rest.request.SurveyExportRequest;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.errors.NtsValidationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ExportService {
    private final ExportRepository exportRepository;

    public ExportService(ExportRepository exportRepository) {
        this.exportRepository = exportRepository;
    }

    public void export(SurveyExportRequest request, String componentType) {
        String checksum = request.getChecksum();
        String conditions = request.getConditions();
        boolean exists = exportRepository.existsByChecksum(checksum);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().orElse("");
        userCode = userCode.toUpperCase();

        if (exists) {
            throw new NtsValidationException("message,",
                String.format("Đã tồn tại dữ liệu với checksum {}", checksum));
        } else {
            ExportEntity exportEntity = new ExportEntity()
                .checksum(checksum)
                .conditions(conditions)
                .component(componentType)
                .userCode(userCode);
            exportRepository.save(exportEntity);
        }
    }

    public ExportDTO getExport(String checksum) {
        if (ObjectUtils.isEmpty(checksum)) {
            throw new NtsValidationException("message", "Checksum không được để trống");
        }
        ExportEntity exportEntity = exportRepository.findByChecksum(checksum);
        ExportDTO exportDTO = ExportMapper.INSTANCE.toDto(exportEntity);
        return exportDTO;
    }
}

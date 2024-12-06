package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.ExportEntity;
import io.yody.yosurvey.survey.domain.enumeration.ExportStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRepository extends JpaRepository<ExportEntity, Long> {
    boolean existsByChecksum(String checksum);
    ExportEntity findByChecksum(String checksum);
    boolean existsByStatus(ExportStatusEnum status);
    List<ExportEntity> findAllByStatus(ExportStatusEnum status);
}

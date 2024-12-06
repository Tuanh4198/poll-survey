package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.SurveyEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the SurveyEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long>, JpaSpecificationExecutor<SurveyEntity> {
    List<SurveyEntity> findAllByIdIn(List<Long> ids);
    List<SurveyEntity> findByApplyTimeBetween(Instant from, Instant to);
    List<SurveyEntity> findByEndTimeBetween(Instant from, Instant to);
    SurveyEntity findByHash(String hash);
}

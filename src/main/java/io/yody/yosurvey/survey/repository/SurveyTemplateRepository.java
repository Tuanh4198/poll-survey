package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SurveyTemplateEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SurveyTemplateRepository extends JpaRepository<SurveyTemplateEntity, Long>, JpaSpecificationExecutor<SurveyTemplateEntity> {}

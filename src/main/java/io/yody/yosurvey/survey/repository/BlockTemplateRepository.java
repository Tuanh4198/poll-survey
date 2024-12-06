package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.BlockTemplateEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockTemplateRepository extends JpaRepository<BlockTemplateEntity, Long> {
}

package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.AssignStrategyTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignStrategyTemplateMapper extends JpaRepository<AssignStrategyTemplateEntity, Long> {
}

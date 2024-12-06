package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.AssignStrategyEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssignStrategyEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssignStrategyRepository extends JpaRepository<AssignStrategyEntity, Long> {}

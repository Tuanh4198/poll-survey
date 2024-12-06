package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.LogicsEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LogicsEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LogicsRepository extends JpaRepository<LogicsEntity, Long> {}

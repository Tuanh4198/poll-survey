package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.BlockEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BlockEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockRepository extends JpaRepository<BlockEntity, Long> {}

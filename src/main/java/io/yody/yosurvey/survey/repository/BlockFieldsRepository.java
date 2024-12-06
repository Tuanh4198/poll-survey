package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the BlockFieldsEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockFieldsRepository extends JpaRepository<BlockFieldsEntity, Long> {
    List<BlockFieldsEntity> findAllBySurveyId(Long surveyId);
}

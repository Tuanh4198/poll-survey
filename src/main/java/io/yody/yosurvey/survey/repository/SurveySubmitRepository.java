package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the SurveySubmitEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SurveySubmitRepository extends JpaRepository<SurveySubmitEntity, Long> {
    @Query(value = "SELECT ss.field_id, COUNT(ss.field_id) FROM survey_submit ss " +
        "WHERE ss.survey_id = :surveyId AND ss.field_id IN :fieldIds AND ss.target = :target " +
        "GROUP BY ss.field_id", nativeQuery = true)
    List<Object[]> countFieldBySurveyIdAndFieldIds(
        @Param("surveyId") Long surveyId,
        @Param("target") String target,
        @Param("fieldIds") List<Long> fieldIds);

    @Query(value = "SELECT * FROM survey_submit ss " +
        "WHERE (:surveyId IS NULL OR ss.survey_id = :surveyId) " +
        "AND (:fieldId IS NULL OR ss.field_id = :fieldId) " +
        "AND (:target IS NULL OR ss.target = :target)" +
        "AND (ss.type <> 'TITLE')", nativeQuery = true)
    Page<SurveySubmitEntity> findAllWithFilters(
        @Param("surveyId") Long surveyId,
        @Param("fieldId") Long fieldId,
        @Param("target") String target,
        Pageable pageable);

}

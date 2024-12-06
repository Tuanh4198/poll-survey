package io.yody.yosurvey.survey.repository;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Spring Data JPA repository for the EmployeeSurveyEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeSurveyRepository extends JpaRepository<EmployeeSurveyEntity, Long>,
    JpaSpecificationExecutor<EmployeeSurveyEntity> {
    List<EmployeeSurveyEntity> findBySurveyId(Long surveyId);
    List<EmployeeSurveyEntity> findByCodeAndSurveyId(String code, Long surveyId);
    List<EmployeeSurveyEntity> findAllBySurveyIdInAndStatus(List<Long> surveyIds, SurveyStatusEnum status);
    @Modifying
    @Transactional
    @Query("UPDATE EmployeeSurveyEntity es SET es.deleted = true " +
        "WHERE (:id is not null AND es.id = :id)")
    void deleteById(@Param("id") Long id);
    @Modifying
    @Transactional
    @Query("UPDATE EmployeeSurveyEntity es SET es.deleted = true " +
        "WHERE (:surveyId is not null AND es.surveyId = :surveyId)" +
        "AND (:status is not null AND es.status = :status)")
    void deleteBySurveyIdAndStatus(@Param("surveyId") Long surveyId, @Param("status") SurveyStatusEnum status);
    @Query("SELECT es " +
        "FROM EmployeeSurveyEntity es " +
        "JOIN SurveyEntity s ON es.surveyId = s.id " +
        "WHERE s.deleted = false " +
        "AND (:code IS NULL OR es.code = :code) " +
        "AND (:search IS NULL OR LOWER(es.code) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(es.target) LIKE LOWER(CONCAT('%', :search, '%'))) " +
        "AND (:surveyId IS NULL OR es.surveyId = :surveyId) " +
        "AND (:status IS NULL OR es.status = :status) " +
        "AND (cast(:applyTime as date) IS NULL OR s.applyTime < :endTime) " +
        "AND (cast(:endTime as date) IS NULL OR s.endTime > :applyTime OR s.endTime IS NULL) " +
        "ORDER BY s.isRequired DESC NULLS LAST, s.endTime ASC NULLS LAST, es.id ASC")
    Page<EmployeeSurveyEntity> findByCriteria(@Param("code") String code,
                                           @Param("search") String search,
                                           @Param("surveyId") Long surveyId,
                                           @Param("status") SurveyStatusEnum status,
                                           @Param("applyTime") Instant applyTime,
                                           @Param("endTime") Instant endTime,
                                           Pageable pageable);
}

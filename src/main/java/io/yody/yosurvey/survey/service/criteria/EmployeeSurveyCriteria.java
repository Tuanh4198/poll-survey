package io.yody.yosurvey.survey.service.criteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import org.springframework.web.bind.annotation.RequestParam;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.time.Instant;

public class EmployeeSurveyCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("survey_id")
    private Long surveyId;
    @JsonProperty("apply_time")
    private Instant applyTime;
    @JsonProperty("end_time")
    private Instant endTime;
    private SurveyStatusEnum status;
    private String search;

    public EmployeeSurveyCriteria() {
    }

    public EmployeeSurveyCriteria(Long surveyId, Instant applyTime, Instant endTime, SurveyStatusEnum status, String search) {
        this.surveyId = surveyId;
        this.applyTime = applyTime;
        this.endTime = endTime;
        this.status = status;
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Instant getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Instant applyTime) {
        this.applyTime = applyTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public SurveyStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SurveyStatusEnum status) {
        this.status = status;
    }
}

package io.yody.yosurvey.survey.service.criteria;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeSurveySubmitCriteria {
    @JsonProperty("survey_id")
    private Long surveyId;
    @JsonProperty("field_id")
    private Long fieldId;
    private String target;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}

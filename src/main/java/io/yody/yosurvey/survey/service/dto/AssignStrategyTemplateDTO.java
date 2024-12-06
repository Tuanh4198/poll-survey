package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class AssignStrategyTemplateDTO implements Serializable {
    private Long id;

    /**
     * Id bài khảo sát
     */
    @NotNull
    @Schema(description = "Id bài khảo sát", required = true)
    private Long surveyId;
    private SurveyTemplateDTO survey;
    private List<MetafieldDTO> metafields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public SurveyTemplateDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyTemplateDTO survey) {
        this.survey = survey;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }
}

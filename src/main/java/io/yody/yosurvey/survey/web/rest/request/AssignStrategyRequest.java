package io.yody.yosurvey.survey.web.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class AssignStrategyRequest implements Serializable {
    private Long id;
    private Long tempId;
    @NotNull
    @Schema(description = "Id bài khảo sát", required = true)
    private Long surveyId;
    private FileRequest fileParticipant;
    private FileRequest fileTargets;
    List<MetafieldRequest> metafields;

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public List<MetafieldRequest> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldRequest> metafields) {
        this.metafields = metafields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTempId() {
        return tempId;
    }

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }

    public FileRequest getFileParticipant() {
        return fileParticipant;
    }

    public void setFileParticipant(FileRequest fileParticipant) {
        this.fileParticipant = fileParticipant;
    }

    public FileRequest getFileTargets() {
        return fileTargets;
    }

    public void setFileTargets(FileRequest fileTargets) {
        this.fileTargets = fileTargets;
    }
}

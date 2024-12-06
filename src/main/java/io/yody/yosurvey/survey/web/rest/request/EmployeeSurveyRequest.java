package io.yody.yosurvey.survey.web.rest.request;

import io.yody.yosurvey.survey.domain.enumeration.ParticipantTypeEnum;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EmployeeSurveyRequest implements Serializable {

    private Long id;

    @NotNull
    @NotEmpty
    private String code;

    @NotNull
    private Long surveyId;

    @NotNull
    @NotEmpty
    private String target;

    private String participantType = ParticipantTypeEnum.OTHER.getKey();

    @NotNull
    private String targetType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}

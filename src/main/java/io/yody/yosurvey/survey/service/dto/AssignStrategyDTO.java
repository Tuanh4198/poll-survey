package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.AssignStrategyEntity} entity.
 */
@Schema(description = "Cặp đối tượng khảo sát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssignStrategyDTO implements Serializable {

    private Long id;

    /**
     * Id bài khảo sát
     */
    @NotNull
    @Schema(description = "Id bài khảo sát", required = true)
    private Long surveyId;
    private SurveyDTO survey;
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

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignStrategyDTO)) {
            return false;
        }

        AssignStrategyDTO assignStrategyDTO = (AssignStrategyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assignStrategyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssignStrategyDTO{" +
            "id=" + getId() +
            ", surveyId=" + getSurveyId() +
            ", surveyId=" + getSurveyId() +
            "}";
    }
}

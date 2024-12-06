package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.SurveyTemplateEntity} entity.
 */
@Schema(description = "Mẫu khảo sát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyTemplateDTO implements Serializable {

    /**
     * Id
     */
    @NotNull
    @Schema(description = "Id", required = true)
    private Long id;

    /**
     * Tiêu đề
     */
    @NotNull
    @Schema(description = "Tiêu đề", required = true)
    private String title;

    /**
     * Mô tả
     */
    @Schema(description = "Mô tả")
    private String description;

    /**
     * Ảnh bìa
     */
    @Schema(description = "Ảnh bìa")
    private String thumbUrl;

    private String presignThumbUrl;

    private Boolean isRequired;
    private List<MetafieldDTO> metafields;
    List<AssignStrategyTemplateDTO> assignStrategies;
    List<BlockTemplateDTO> blocks;
    /**
     * Số lượt sử dụng
     */
    @Schema(description = "Số lượt sử dụng")
    private Long usedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Long usedTime) {
        this.usedTime = usedTime;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }

    public List<AssignStrategyTemplateDTO> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyTemplateDTO> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<BlockTemplateDTO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockTemplateDTO> blocks) {
        this.blocks = blocks;
    }

    public String getPresignThumbUrl() {
        return presignThumbUrl;
    }

    public void setPresignThumbUrl(String presignThumbUrl) {
        this.presignThumbUrl = presignThumbUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyTemplateDTO)) {
            return false;
        }

        SurveyTemplateDTO surveyTemplateDTO = (SurveyTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, surveyTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyTemplateDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbUrl='" + getThumbUrl() + "'" +
            ", usedTime=" + getUsedTime() +
            "}";
    }
}

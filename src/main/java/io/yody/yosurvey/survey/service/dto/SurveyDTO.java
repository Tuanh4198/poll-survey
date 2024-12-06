package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.SurveyEntity} entity.
 */
@Schema(description = "Bài khảo sát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyDTO implements Serializable {

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
    @NotNull
    @Schema(description = "Ảnh bìa", required = true)
    private String thumbUrl;

    private String presignThumbUrl;

    /**
     * Ngày bắt đầu
     */
    @NotNull
    @Schema(description = "Ngày bắt đầu", required = true)
    private Instant applyTime;

    /**
     * Ngày kết thúc
     */
    @NotNull
    @Schema(description = "Ngày kết thúc", required = true)
    private Instant endTime;

    /**
     * Bắt buộc hoàn thành
     */
    @NotNull
    @Schema(description = "Bắt buộc hoàn thành", required = true)
    private Boolean isRequired;

    List<AssignStrategyDTO> assignStrategies;
    List<BlockDTO> blocks;
    List<MetafieldDTO> metafields;

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

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public List<AssignStrategyDTO> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyDTO> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<BlockDTO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockDTO> blocks) {
        this.blocks = blocks;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
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
        if (!(o instanceof SurveyDTO)) {
            return false;
        }

        SurveyDTO surveyDTO = (SurveyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, surveyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbUrl='" + getThumbUrl() + "'" +
            ", applyTime='" + getApplyTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            "}";
    }
}

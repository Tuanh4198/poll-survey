package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.BlockEntity} entity.
 */
@Schema(description = "Block khảo sát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlockDTO implements Serializable {

    /**
     * Id
     */
    @NotNull
    @Schema(description = "Id", required = true)
    private Long id;

    /**
     * Loại component
     */
    @NotNull
    @Schema(description = "Loại component", required = true)
    private ComponentTypeEnum type;

    /**
     * Id survey gốc
     */
    @NotNull
    @Schema(description = "Id survey gốc", required = true)
    private Long surveyId;

    /**
     * Trang
     */
    @NotNull
    @Schema(description = "Trang", required = true)
    private Long pageNum;

    /**
     * Thứ tự
     */
    @NotNull
    @Schema(description = "Thứ tự", required = true)
    private Long num;

    private SurveyDTO survey;
    private List<BlockFieldsDTO> blockFields;
    private List<MetafieldDTO> metafields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComponentTypeEnum getType() {
        return type;
    }

    public void setType(ComponentTypeEnum type) {
        this.type = type;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }

    public List<BlockFieldsDTO> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsDTO> blockFields) {
        this.blockFields = blockFields;
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
        if (!(o instanceof BlockDTO)) {
            return false;
        }

        BlockDTO blockDTO = (BlockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, blockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", surveyId=" + getSurveyId() +
            ", pageNum=" + getPageNum() +
            ", num=" + getNum() +
            ", surveyId=" + getSurveyId() +
            "}";
    }
}

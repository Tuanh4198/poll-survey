package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.SurveySubmitEntity} entity.
 */
@Schema(description = "Kết quả submit")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveySubmitDTO implements Serializable {

    /**
     * Id
     */
    @NotNull
    @Schema(description = "Id", required = true)
    private Long id;

    /**
     * Mã nhân viên
     */
    @NotNull
    @Schema(description = "Mã nhân viên", required = true)
    private String code;

    /**
     * Tên nhân viên
     */
    @NotNull
    @Schema(description = "Tên nhân viên", required = true)
    private String name;

    /**
     * Mã bài khảo sát gốc
     */
    @NotNull
    @Schema(description = "Mã bài khảo sát gốc", required = true)
    private Long surveyId;

    /**
     * Mã block
     */
    @NotNull
    @Schema(description = "Mã block", required = true)
    private Long blockId;

    /**
     * Loại block
     */
    @NotNull
    @Schema(description = "Loại block", required = true)
    private ComponentTypeEnum type;

    /**
     * Mã field
     */
    @NotNull
    @Schema(description = "Mã field", required = true)
    private Long fieldId;

    /**
     * Tên field
     */
    @NotNull
    @Schema(description = "Tên field", required = true)
    private String fieldName;

    /**
     * Giá trị submit
     */
    @NotNull
    @Schema(description = "Giá trị submit", required = true)
    private String fieldValue;

    private BlockFieldsDTO blockField;

    private BlockDTO block;

    private SurveyDTO survey;
    private String target;
    private String targetName;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public ComponentTypeEnum getType() {
        return type;
    }

    public void setType(ComponentTypeEnum type) {
        this.type = type;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public BlockFieldsDTO getBlockField() {
        return blockField;
    }

    public void setBlockField(BlockFieldsDTO blockField) {
        this.blockField = blockField;
    }

    public BlockDTO getBlock() {
        return block;
    }

    public void setBlock(BlockDTO block) {
        this.block = block;
    }

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveySubmitDTO)) {
            return false;
        }

        SurveySubmitDTO surveySubmitDTO = (SurveySubmitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, surveySubmitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveySubmitDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", surveyId=" + getSurveyId() +
            ", blockId=" + getBlockId() +
            ", type='" + getType() + "'" +
            ", fieldId=" + getFieldId() +
            ", fieldName='" + getFieldName() + "'" +
            ", fieldValue='" + getFieldValue() + "'" +
            ", fieldId=" + getFieldId() +
            ", blockId=" + getBlockId() +
            ", surveyId=" + getSurveyId() +
            "}";
    }
}

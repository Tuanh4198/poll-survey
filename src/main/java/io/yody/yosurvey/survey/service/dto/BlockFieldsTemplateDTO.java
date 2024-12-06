package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class BlockFieldsTemplateDTO implements Serializable {
    /**
     * Id
     */
    @NotNull
    @Schema(description = "Id", required = true)
    private Long id;

    /**
     * Id Block gốc
     */
    @NotNull
    @Schema(description = "Id Block gốc", required = true)
    private Long blockId;

    /**
     * Id Survey gốc
     */
    @NotNull
    @Schema(description = "Id Survey gốc", required = true)
    private Long surveyId;

    /**
     * fieldName
     */
    @NotNull
    @Schema(description = "fieldName", required = true)
    private String fieldName;

    /**
     * fieldValue
     */
    @NotNull
    @Schema(description = "fieldValue", required = true)
    private String fieldValue;

    /**
     * Loại field
     */
    @NotNull
    @Schema(description = "Loại field", required = true)
    private FieldTypeEnum type;

    private BlockTemplateDTO block;
    private List<MetafieldDTO> metafields;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBlockId() {
        return blockId;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
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

    public FieldTypeEnum getType() {
        return type;
    }

    public void setType(FieldTypeEnum type) {
        this.type = type;
    }

    public BlockTemplateDTO getBlock() {
        return block;
    }

    public void setBlock(BlockTemplateDTO block) {
        this.block = block;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }
}

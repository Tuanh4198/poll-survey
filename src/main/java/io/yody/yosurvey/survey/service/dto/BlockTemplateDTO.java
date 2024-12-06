package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class BlockTemplateDTO implements Serializable {
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

    private SurveyTemplateDTO survey;
    private List<BlockFieldsTemplateDTO> blockFields;
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

    public SurveyTemplateDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyTemplateDTO survey) {
        this.survey = survey;
    }

    public List<BlockFieldsTemplateDTO> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsTemplateDTO> blockFields) {
        this.blockFields = blockFields;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }
}

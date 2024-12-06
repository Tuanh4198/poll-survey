package io.yody.yosurvey.survey.web.rest.request;

import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;

import java.io.Serializable;
import java.util.List;

public class BlockFieldsRequest implements Serializable {
    private Long id;
    private Long tempId;
    private Long blockId;
    private Long surveyId;
    private String fieldName;
    private String fieldValue;
    private FieldTypeEnum type;
    private List<MetafieldRequest> metafields;

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

    public List<MetafieldRequest> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldRequest> metafields) {
        this.metafields = metafields;
    }
}

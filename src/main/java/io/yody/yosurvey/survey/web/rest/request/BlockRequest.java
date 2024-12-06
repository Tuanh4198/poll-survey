package io.yody.yosurvey.survey.web.rest.request;

import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;

import java.io.Serializable;
import java.util.List;

public class BlockRequest implements Serializable {
    private Long id;
    private Long tempId;
    private ComponentTypeEnum type;
    private Long surveyId;
    private Long pageNum;
    private Long num;
    private List<BlockFieldsRequest> blockFields;
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

    public List<BlockFieldsRequest> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsRequest> blockFields) {
        this.blockFields = blockFields;
    }

    public List<MetafieldRequest> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldRequest> metafields) {
        this.metafields = metafields;
    }
}

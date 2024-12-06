package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;
import io.yody.yosurvey.survey.web.rest.request.BlockFieldsRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import org.nentangso.core.service.errors.NtsValidationException;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockFieldsBO {
    private Long id;
    private Long blockId;
    private Long surveyId;
    private String fieldName;
    private String fieldValue;
    private FieldTypeEnum type;
    private List<BlockFieldsMetafieldBO> metafields;

    public BlockFieldsBO() {
    }

    public BlockFieldsBO(Long id, Long blockId, Long surveyId, String fieldName, String fieldValue, FieldTypeEnum type) {
        this.id = id;
        this.blockId = blockId;
        this.surveyId = surveyId;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.type = type;
    }
    public BlockFieldsBO(BlockBO root, BlockFieldsRequest request) {
        this.id = request.getTempId();
        this.blockId = root.getId();
        this.surveyId = root.getSurveyId();
        this.fieldName = request.getFieldName();
        this.fieldValue = request.getFieldValue();
        this.type = request.getType();
        _setMetafields(request.getMetafields());
    }
    private void _setMetafields(List<MetafieldRequest> requests) {
        this.metafields = new ArrayList<>();
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.metafields.add(new BlockFieldsMetafieldBO(this, request));
        });
    }
    private void validateFieldName() {
        if (ObjectUtils.isEmpty(fieldName)) {
            throw new NtsValidationException("message", "Tên trường không được để trống");
        }
    }
    private void validateMetafields() {
        for (BlockFieldsMetafieldBO metafieldBO : metafields) {
            metafieldBO.validate();
        }
    }
    public void validate() {
        validateFieldName();
        validateMetafields();
    }
    public void update(BlockBO root, BlockFieldsRequest request) {
        this.id = request.getTempId();
        this.blockId = root.getId();
        this.surveyId = root.getSurveyId();
        this.fieldName = request.getFieldName();
        this.fieldValue = request.getFieldValue();
        this.type = request.getType();
        _setMetafields(request.getMetafields());
    }
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

    public List<BlockFieldsMetafieldBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<BlockFieldsMetafieldBO> metafields) {
        this.metafields = metafields;
    }
}

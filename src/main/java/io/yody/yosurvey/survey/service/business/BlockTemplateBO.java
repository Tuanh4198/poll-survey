package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import io.yody.yosurvey.survey.service.helpers.UpdateListHelper;
import io.yody.yosurvey.survey.web.rest.request.BlockFieldsRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import org.nentangso.core.service.errors.NtsValidationException;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockTemplateBO {
    private Long id;
    private ComponentTypeEnum type;
    private Long surveyId;
    private Long pageNum;
    private Long num;
    private List<BlockFieldsTemplateBO> blockFields;
    private List<BlockMetafieldTemplateBO> metafields;

    public BlockTemplateBO(Long id, ComponentTypeEnum type, Long surveyId, Long pageNum, Long num,
                   List<BlockFieldsTemplateBO> blockFields, List<BlockMetafieldTemplateBO> metafields) {
        this.id = id;
        this.type = type;
        this.surveyId = surveyId;
        this.pageNum = pageNum;
        this.num = num;
        this.blockFields = blockFields;
        this.metafields = metafields;
    }
    public BlockTemplateBO(SurveyTemplateAggregate root, BlockRequest request) {
        this.id = request.getTempId();
        this.type = request.getType();
        this.surveyId = root.getId();
        this.pageNum = request.getPageNum();
        this.num = request.getNum();
        _setBlockFields(request.getBlockFields());
        _setMetafields(request.getMetafields());
    }

    public BlockTemplateBO() {
    }

    public void update(SurveyTemplateAggregate aggregate, BlockRequest request) {
        this.id = request.getTempId();
        this.type = request.getType();
        this.surveyId = aggregate.getId();
        this.pageNum = request.getPageNum();
        this.num = request.getNum();
        updateBlockFields(request.getBlockFields());
        _setMetafields(request.getMetafields());
    }
    private void updateBlockFields(List<BlockFieldsRequest> blockFieldRequest) {
        UpdateListHelper.updateListBO(this, blockFields, blockFieldRequest,
            BlockFieldsTemplateBO::getId, BlockFieldsRequest::getTempId, BlockFieldsTemplateBO::update, BlockFieldsTemplateBO::new);
    }
    private void _setBlockFields(List<BlockFieldsRequest> requests) {
        if (ObjectUtils.isEmpty(this.blockFields)) {
            this.blockFields = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.blockFields.add(new BlockFieldsTemplateBO(this, request));
        });
    }
    private void _setMetafields(List<MetafieldRequest> requests) {
        if (ObjectUtils.isEmpty(this.metafields)) {
            this.metafields = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.metafields.add(new BlockMetafieldTemplateBO(this, request));
        });
    }
    private void validateNum() {
        if (num <= 0 || pageNum <= 0) {
            throw new NtsValidationException("message", "Thứ tự phải lớn hơn 0");
        }
        if (num > 100 || pageNum > 100) {
            throw new NtsValidationException("message", "Thứ tự phải nhỏ hơn 100");
        }
    }
    private void validateBlockFields() {
        for (BlockFieldsTemplateBO blockFieldsBO : blockFields) {
            blockFieldsBO.validate();
        }
    }
    private void validateMetafields() {
        for (BlockMetafieldTemplateBO metafieldBO : metafields) {
            metafieldBO.validate();
        }
    }
    public void validate() {
        validateNum();
        validateBlockFields();
        validateMetafields();
    }
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

    public List<BlockFieldsTemplateBO> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsTemplateBO> blockFields) {
        this.blockFields = blockFields;
    }

    public List<BlockMetafieldTemplateBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<BlockMetafieldTemplateBO> metafields) {
        this.metafields = metafields;
    }
}

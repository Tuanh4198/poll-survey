package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.service.helpers.UpdateListHelper;
import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
import org.nentangso.core.service.errors.NtsValidationException;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SurveyTemplateAggregate {
    private Long id;
    private String title;
    private String description;
    private String thumbUrl;
    private Instant applyTime;
    private Instant endTime;
    private Boolean isRequired;
    private List<AssignStrategyTemplateBO> assignStrategies;
    private List<SurveyTemplateMetafieldBO> metafields;
    private List<BlockTemplateBO> blocks;

    public SurveyTemplateAggregate(Long id, String title, String description, String thumbUrl, Instant applyTime, Instant endTime, Boolean isRequired,
                                   List<AssignStrategyTemplateBO> assignStrategies, List<SurveyTemplateMetafieldBO> metafields, List<BlockTemplateBO> blocks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbUrl = thumbUrl;
        this.applyTime = applyTime;
        this.endTime = endTime;
        this.isRequired = isRequired;
        this.assignStrategies = assignStrategies;
        this.metafields = metafields;
        this.blocks = blocks;
    }

    public SurveyTemplateAggregate(SurveyRequest request) {
        this.id = request.getTempId();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.thumbUrl = request.getThumbUrl();
        this.isRequired = request.getIsRequired();
        this.applyTime = request.getApplyTime();
        this.endTime = request.getEndTime();
        _setAssignStrategies(request.getAssignStrategies());
        _setBlocks(request.getBlocks());
        _setMetafields(request.getMetafields());
    }

    public SurveyTemplateAggregate() {
    }

    public void update(SurveyRequest request) {
        this.id = request.getTempId();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.thumbUrl = request.getThumbUrl();
        this.applyTime = request.getApplyTime();
        this.endTime = request.getEndTime();
        this.isRequired = request.getIsRequired();
        updateStrategies(request.getAssignStrategies());
        updateBlocks(request.getBlocks());
        updateMetafields(request.getMetafields());
    }
    private void updateStrategies(List<AssignStrategyRequest> assignStrategyRequests) {
        UpdateListHelper.updateListBO(this, assignStrategies, assignStrategyRequests,
            AssignStrategyTemplateBO::getId, AssignStrategyRequest::getTempId, AssignStrategyTemplateBO::update, AssignStrategyTemplateBO::new);
    }
    private void updateBlocks(List<BlockRequest> blockRequests) {
        UpdateListHelper.updateListBO(this, blocks, blockRequests,
            BlockTemplateBO::getId, BlockRequest::getTempId, BlockTemplateBO::update, BlockTemplateBO::new);
    }
    private void updateMetafields(List<MetafieldRequest> metafieldRequests) {
        _setMetafields(metafieldRequests);
    }
    private void validateTime() {
        if (!Objects.isNull(endTime)) {
            if (applyTime.isAfter(endTime)) {
                throw new NtsValidationException("message", "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
            }
        }
    }
    private void validateAssignStrategy() {
        for (AssignStrategyTemplateBO assignStrategyBO : assignStrategies) {
            assignStrategyBO.validate();
        }
    }
    private void validateBlocks() {
        for (BlockTemplateBO block : blocks) {
            block.validate();
        }
    }
    private void validateMetafields() {
        for (SurveyTemplateMetafieldBO metafield : metafields) {
            metafield.validate();
        }
    }
    public void validate() {
        validateTime();
        validateAssignStrategy();
        validateBlocks();
        validateMetafields();
    }
    private void _setAssignStrategies(List<AssignStrategyRequest> requests) {
        if (ObjectUtils.isEmpty(this.assignStrategies)) {
            this.assignStrategies = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.assignStrategies.add(new AssignStrategyTemplateBO(this, request));
        });
    }
    private void _setBlocks(List<BlockRequest> requests) {
        if (ObjectUtils.isEmpty(this.blocks)) {
            this.blocks = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.blocks.add(new BlockTemplateBO(this, request));
        });
    }
    private void _setMetafields(List<MetafieldRequest> requests) {
        if (ObjectUtils.isEmpty(this.metafields)) {
            this.metafields = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.metafields.add(new SurveyTemplateMetafieldBO(this, request));
        });
    }
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

    public void setIsRequired(Boolean required) {
        isRequired = required;
    }

    public List<AssignStrategyTemplateBO> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyTemplateBO> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<SurveyTemplateMetafieldBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<SurveyTemplateMetafieldBO> metafields) {
        this.metafields = metafields;
    }

    public List<BlockTemplateBO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockTemplateBO> blocks) {
        this.blocks = blocks;
    }
}

package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.service.helpers.HashGenerator;
import io.yody.yosurvey.survey.service.helpers.UpdateListHelper;
import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.BlockRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
import org.nentangso.core.service.errors.NtsValidationException;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SurveyAggregate {
    private Long id;
    private String hash;
    private String title;
    private String description;
    private String thumbUrl;
    private Instant applyTime;
    private Instant endTime;
    private Boolean isRequired;
    private List<AssignStrategyBO> assignStrategies;
    private List<SurveyMetafieldBO> metafields;
    private List<BlockBO> blocks;

    public SurveyAggregate() {
    }

    public SurveyAggregate(Long id, String title, String description, String thumbUrl, Instant applyTime, Instant endTime, Boolean isRequired, List<AssignStrategyBO> assignStrategies, List<SurveyMetafieldBO> metafields, List<BlockBO> blocks) {
        this.id = id;
        generateHash();
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

    public SurveyAggregate(SurveyRequest request) {
        this.id = request.getTempId();
        generateHash();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.thumbUrl = request.getThumbUrl();
        this.applyTime = request.getApplyTime();
        this.endTime = request.getEndTime();
        this.isRequired = request.getIsRequired();
        _setAssignStrategies(request.getAssignStrategies());
        _setBlocks(request.getBlocks());
        _setMetafields(request.getMetafields());
    }
    private void generateHash() {
        if (ObjectUtils.isEmpty(hash)) {
            setHash(HashGenerator.generateUniqueHash());
        }
    }
    private void validateTime() {
        if (Objects.isNull(applyTime)) {
            throw new NtsValidationException("message", "Phải có thời gian bắt đầu");
        }
        if (!Objects.isNull(endTime)) {
            if (applyTime.isAfter(endTime) || applyTime.equals(endTime)) {
                throw new NtsValidationException("message", "Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc");
            }
        }
    }
    private void validateAssignStrategy() {
        for (AssignStrategyBO assignStrategyBO : assignStrategies) {
            assignStrategyBO.validate();
        }
    }
    private void validateBlocks() {
        for (BlockBO block : blocks) {
            block.validate();
        }
    }
    private void validateMetafields() {
        for (SurveyMetafieldBO metafield : metafields) {
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
            this.assignStrategies.add(new AssignStrategyBO(this, request));
        });
    }
    private void _setBlocks(List<BlockRequest> requests) {
        if (ObjectUtils.isEmpty(this.blocks)) {
            this.blocks = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.blocks.add(new BlockBO(this, request));
        });
    }
    private void _setMetafields(List<MetafieldRequest> requests) {
        this.metafields = new ArrayList<>();
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(request -> {
            this.metafields.add(new SurveyMetafieldBO(this, request));
        });
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
            AssignStrategyBO::getId, AssignStrategyRequest::getTempId, AssignStrategyBO::update, AssignStrategyBO::new);
    }
    private void updateBlocks(List<BlockRequest> blockRequests) {
        UpdateListHelper.updateListBO(this, blocks, blockRequests,
            BlockBO::getId, BlockRequest::getTempId, BlockBO::update, BlockBO::new);
    }
    private void updateMetafields(List<MetafieldRequest> metafieldRequests) {
        _setMetafields(metafieldRequests);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
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

    public List<AssignStrategyBO> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyBO> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<SurveyMetafieldBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<SurveyMetafieldBO> metafields) {
        this.metafields = metafields;
    }

    public List<BlockBO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockBO> blocks) {
        this.blocks = blocks;
    }
}

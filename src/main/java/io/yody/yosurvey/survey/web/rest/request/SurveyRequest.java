package io.yody.yosurvey.survey.web.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class SurveyRequest implements Serializable {
    private Long id;
    private Long tempId;
    /**
     * Tiêu đề
     */
    @NotNull
    @NotEmpty
    @Schema(description = "Tiêu đề", required = true)
    private String title;

    /**
     * Mô tả
     */
    @Schema(description = "Mô tả")
    private String description;

    /**
     * Ảnh bìa
     */
    @NotNull
    @Schema(description = "Ảnh bìa", required = true)
    private String thumbUrl;

    /**
     * Ngày bắt đầu
     */
    @NotNull
    @Schema(description = "Ngày bắt đầu", required = true)
    private Instant applyTime;

    /**
     * Ngày kết thúc
     */
    @Schema(description = "Ngày kết thúc", required = true)
    private Instant endTime;

    /**
     * Bắt buộc hoàn thành
     */
    @NotNull
    @Schema(description = "Bắt buộc hoàn thành", required = true)
    private Boolean isRequired;

    @NotNull
    @NotEmpty
    List<AssignStrategyRequest> assignStrategies;
    List<MetafieldRequest> metafields;
    List<BlockRequest> blocks;

    public SurveyRequest() {
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

    public List<AssignStrategyRequest> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyRequest> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<MetafieldRequest> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldRequest> metafields) {
        this.metafields = metafields;
    }

    public List<BlockRequest> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockRequest> blocks) {
        this.blocks = blocks;
    }

    public Long getTempId() {
        return tempId;
    }

    public void setTempId(Long tempId) {
        this.tempId = tempId;
    }
}

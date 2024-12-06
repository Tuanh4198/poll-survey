package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.EmployeeSurveyEntity} entity.
 */
@Schema(description = "Bài khảo sát cho nhân viên")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeSurveyDTO implements Serializable {

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

    @Schema(description = "Loại đối tượng tham gia")
    private String participantType;

    /**
     * Mã khảo sát gốc
     */
    @NotNull
    @Schema(description = "Mã khảo sát gốc", required = true)
    private Long surveyId;

    /**
     * Trạng thái
     */
    @NotNull
    @Schema(description = "Trạng thái", required = true)
    private SurveyStatusEnum status;

    @Schema(description = "Đối tượng tham gia")
    private String target;
    @Schema(description = "Loại đối tượng tham gia")
    private String targetType;
    @Schema(description = "Tên đối tượng tham gia")
    private String targetName;

    /** survey information */
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
    @NotNull
    @Schema(description = "Ngày kết thúc", required = true)
    private Instant endTime;

    /**
     * Bắt buộc hoàn thành
     */
    @NotNull
    @Schema(description = "Bắt buộc hoàn thành", required = true)
    private Boolean isRequired;

    List<AssignStrategyDTO> assignStrategies;
    List<BlockDTO> blocks;
    List<MetafieldDTO> metafields;
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

    public SurveyStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SurveyStatusEnum status) {
        this.status = status;
    }


    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
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

    public List<AssignStrategyDTO> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyDTO> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<BlockDTO> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockDTO> blocks) {
        this.blocks = blocks;
    }

    public List<MetafieldDTO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<MetafieldDTO> metafields) {
        this.metafields = metafields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeSurveyDTO)) {
            return false;
        }

        EmployeeSurveyDTO employeeSurveyDTO = (EmployeeSurveyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, employeeSurveyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeSurveyDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", surveyId=" + getSurveyId() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

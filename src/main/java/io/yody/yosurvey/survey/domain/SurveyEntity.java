package io.yody.yosurvey.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Bài khảo sát
 */
@Entity
@Table(name = "survey")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hash", nullable = false)
    private String hash;
    /**
     * Tiêu đề
     */
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Mô tả
     */
    @Column(name = "description")
    private String description;

    /**
     * Ảnh bìa
     */
    @NotNull
    @Column(name = "thumb_url", nullable = false)
    private String thumbUrl;

    /**
     * Ngày bắt đầu
     */
    @NotNull
    @Column(name = "apply_time", nullable = false)
    private Instant applyTime;

    /**
     * Ngày kết thúc
     */
    @Column(name = "end_time")
    private Instant endTime;

    /**
     * Bắt buộc hoàn thành
     */
    @NotNull
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignStrategyEntity> assignStrategies;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockEntity> blocks;
    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public SurveyEntity() {
    }

    public SurveyEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public SurveyEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public SurveyEntity hash(String hash) {
        setHash(hash);
        return this;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return this.title;
    }

    public SurveyEntity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public SurveyEntity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbUrl() {
        return this.thumbUrl;
    }

    public SurveyEntity thumbUrl(String thumbUrl) {
        this.setThumbUrl(thumbUrl);
        return this;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Instant getApplyTime() {
        return this.applyTime;
    }

    public SurveyEntity applyTime(Instant applyTime) {
        this.setApplyTime(applyTime);
        return this;
    }

    public void setApplyTime(Instant applyTime) {
        this.applyTime = applyTime;
    }

    public Instant getEndTime() {
        return this.endTime;
    }

    public SurveyEntity endTime(Instant endTime) {
        this.setEndTime(endTime);
        return this;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsRequired() {
        return this.isRequired;
    }

    public SurveyEntity isRequired(Boolean isRequired) {
        this.setIsRequired(isRequired);
        return this;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public List<AssignStrategyEntity> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyEntity> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<BlockEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockEntity> blocks) {
        this.blocks = blocks;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SurveyEntity)) {
            return false;
        }
        return id != null && id.equals(((SurveyEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyEntity{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbUrl='" + getThumbUrl() + "'" +
            ", applyTime='" + getApplyTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            ", isRequired='" + getIsRequired() + "'" +
            "}";
    }
}

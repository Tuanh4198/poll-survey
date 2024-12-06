package io.yody.yosurvey.survey.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Mẫu khảo sát
 */
@Entity
@Table(name = "survey_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveyTemplateEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

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
    @Column(name = "thumb_url")
    private String thumbUrl;

    /**
     * Số lượt sử dụng
     */
    @Column(name = "used_time")
    private Long usedTime;
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<AssignStrategyTemplateEntity> assignStrategies;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    private List<BlockTemplateEntity> blocks;
    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public SurveyTemplateEntity() {
    }

    public SurveyTemplateEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public SurveyTemplateEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public SurveyTemplateEntity title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public SurveyTemplateEntity description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbUrl() {
        return this.thumbUrl;
    }

    public SurveyTemplateEntity thumbUrl(String thumbUrl) {
        this.setThumbUrl(thumbUrl);
        return this;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Long getUsedTime() {
        return this.usedTime;
    }

    public SurveyTemplateEntity usedTime(Long usedTime) {
        this.setUsedTime(usedTime);
        return this;
    }

    public void setUsedTime(Long usedTime) {
        this.usedTime = usedTime;
    }

    public List<AssignStrategyTemplateEntity> getAssignStrategies() {
        return assignStrategies;
    }

    public void setAssignStrategies(List<AssignStrategyTemplateEntity> assignStrategies) {
        this.assignStrategies = assignStrategies;
    }

    public List<BlockTemplateEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockTemplateEntity> blocks) {
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
        if (!(o instanceof SurveyTemplateEntity)) {
            return false;
        }
        return id != null && id.equals(((SurveyTemplateEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveyTemplateEntity{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbUrl='" + getThumbUrl() + "'" +
            ", usedTime=" + getUsedTime() +
            "}";
    }
}

package io.yody.yosurvey.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Block khảo sát
 */
@Entity
@Table(name = "block")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlockEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Loại component
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComponentTypeEnum type;

    /**
     * Id survey gốc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private SurveyEntity survey;

    /**
     * Trang
     */
    @NotNull
    @Column(name = "page_num", nullable = false)
    private Long pageNum;

    /**
     * Thứ tự
     */
    @NotNull
    @Column(name = "num", nullable = false)
    private Long num;

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockFieldsEntity> blockFields;
    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public BlockEntity() {
    }

    public BlockEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public BlockEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComponentTypeEnum getType() {
        return this.type;
    }

    public BlockEntity type(ComponentTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(ComponentTypeEnum type) {
        this.type = type;
    }

    public SurveyEntity getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyEntity survey) {
        this.survey = survey;
    }

    public Long getPageNum() {
        return this.pageNum;
    }

    public BlockEntity pageNum(Long pageNum) {
        this.setPageNum(pageNum);
        return this;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getNum() {
        return this.num;
    }

    public BlockEntity num(Long num) {
        this.setNum(num);
        return this;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public List<BlockFieldsEntity> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsEntity> blockFields) {
        this.blockFields = blockFields;
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
        if (!(o instanceof BlockEntity)) {
            return false;
        }
        return id != null && id.equals(((BlockEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockEntity{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", pageNum=" + getPageNum() +
            ", num=" + getNum() +
            "}";
    }
}

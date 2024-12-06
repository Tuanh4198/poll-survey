package io.yody.yosurvey.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Các trường trong block khảo sát
 */
@Entity
@Table(name = "block_fields")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlockFieldsEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @NotNull
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Id Block gốc
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "block_id", nullable = false)
    private BlockEntity block;

    /**
     * Id Survey gốc
     */
    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    /**
     * fieldName
     */
    @NotNull
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * fieldValue
     */
    @NotNull
    @Column(name = "field_value", nullable = false)
    private String fieldValue;

    /**
     * Loại field
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FieldTypeEnum type;
    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BlockFieldsEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BlockEntity getBlock() {
        return block;
    }

    public void setBlock(BlockEntity block) {
        this.block = block;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public BlockFieldsEntity surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public BlockFieldsEntity fieldName(String fieldName) {
        this.setFieldName(fieldName);
        return this;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

    public BlockFieldsEntity fieldValue(String fieldValue) {
        this.setFieldValue(fieldValue);
        return this;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public FieldTypeEnum getType() {
        return this.type;
    }

    public BlockFieldsEntity type(FieldTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(FieldTypeEnum type) {
        this.type = type;
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
        if (!(o instanceof BlockFieldsEntity)) {
            return false;
        }
        return id != null && id.equals(((BlockFieldsEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockFieldsEntity{" +
            "id=" + getId() +
//            ", blockId=" + getBlockId() +
            ", surveyId=" + getSurveyId() +
            ", fieldName='" + getFieldName() + "'" +
            ", fieldValue='" + getFieldValue() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}

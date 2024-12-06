package io.yody.yosurvey.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Kết quả submit
 */
@Entity
@Table(name = "survey_submit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SurveySubmitEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Mã nhân viên
     */
    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Tên nhân viên
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "target")
    private String target;

    /**
     * Tên nhân viên
     */
    @Column(name = "target_name")
    private String targetName;

    /**
     * Mã bài khảo sát gốc
     */
    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    /**
     * Mã block
     */
    @NotNull
    @Column(name = "block_id", nullable = false)
    private Long blockId;

    /**
     * Loại block
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComponentTypeEnum type;

    /**
     * Mã field
     */
    @NotNull
    @Column(name = "field_id", nullable = false)
    private Long fieldId;

    /**
     * Tên field
     */
    @NotNull
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Giá trị submit
     */
    @NotNull
    @Column(name = "field_value", nullable = false)
    private String fieldValue;

    @NotNull
    @Column(name = "field_type", nullable = false)
    private String fieldType;

    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SurveySubmitEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public SurveySubmitEntity code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public SurveySubmitEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public SurveySubmitEntity surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getBlockId() {
        return this.blockId;
    }

    public SurveySubmitEntity blockId(Long blockId) {
        this.setBlockId(blockId);
        return this;
    }

    public void setBlockId(Long blockId) {
        this.blockId = blockId;
    }

    public ComponentTypeEnum getType() {
        return this.type;
    }

    public SurveySubmitEntity type(ComponentTypeEnum type) {
        this.setType(type);
        return this;
    }

    public void setType(ComponentTypeEnum type) {
        this.type = type;
    }

    public Long getFieldId() {
        return this.fieldId;
    }

    public SurveySubmitEntity fieldId(Long fieldId) {
        this.setFieldId(fieldId);
        return this;
    }

    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public SurveySubmitEntity fieldName(String fieldName) {
        this.setFieldName(fieldName);
        return this;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return this.fieldValue;
    }

    public SurveySubmitEntity fieldValue(String fieldValue) {
        this.setFieldValue(fieldValue);
        return this;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldType() {
        return fieldType;
    }
    public SurveySubmitEntity fieldType(String fieldType) {
        this.setFieldType(fieldType);
        return this;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getTarget() {
        return target;
    }

    public SurveySubmitEntity target(String target) {
        setTarget(target);
        return this;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetName() {
        return targetName;
    }
    public SurveySubmitEntity targetName(String targetName) {
        setTargetName(targetName);
        return this;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
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
        if (!(o instanceof SurveySubmitEntity)) {
            return false;
        }
        return id != null && id.equals(((SurveySubmitEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SurveySubmitEntity{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", surveyId=" + getSurveyId() +
            ", blockId=" + getBlockId() +
            ", type='" + getType() + "'" +
            ", fieldId=" + getFieldId() +
            ", fieldName='" + getFieldName() + "'" +
            ", fieldValue='" + getFieldValue() + "'" +
            "}";
    }
}

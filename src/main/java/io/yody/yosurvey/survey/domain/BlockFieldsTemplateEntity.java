package io.yody.yosurvey.survey.domain;

import io.yody.yosurvey.survey.domain.enumeration.FieldTypeEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "block_fields_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BlockFieldsTemplateEntity extends AbstractAuditingEntity implements Serializable {
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
    private BlockTemplateEntity block;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BlockTemplateEntity getBlock() {
        return block;
    }

    public void setBlock(BlockTemplateEntity block) {
        this.block = block;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public FieldTypeEnum getType() {
        return type;
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
}

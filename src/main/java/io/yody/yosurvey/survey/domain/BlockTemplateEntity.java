package io.yody.yosurvey.survey.domain;

import io.yody.yosurvey.survey.domain.enumeration.ComponentTypeEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "block_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BlockTemplateEntity extends AbstractAuditingEntity implements Serializable {
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
    private SurveyTemplateEntity survey;

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

    @OneToMany(mappedBy = "block", cascade = CascadeType.ALL)
    private List<BlockFieldsTemplateEntity> blockFields;
    @NotNull
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public BlockTemplateEntity() {
    }

    public BlockTemplateEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ComponentTypeEnum getType() {
        return type;
    }

    public void setType(ComponentTypeEnum type) {
        this.type = type;
    }

    public SurveyTemplateEntity getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyTemplateEntity survey) {
        this.survey = survey;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public List<BlockFieldsTemplateEntity> getBlockFields() {
        return blockFields;
    }

    public void setBlockFields(List<BlockFieldsTemplateEntity> blockFields) {
        this.blockFields = blockFields;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}

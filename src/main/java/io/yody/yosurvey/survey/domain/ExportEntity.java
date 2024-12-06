package io.yody.yosurvey.survey.domain;

import io.yody.yosurvey.survey.domain.enumeration.ExportStatusEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nentangso.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "exports")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExportEntity extends AbstractAuditingEntity implements Cloneable {
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

    @NotNull
    @Column(name = "checksum", nullable = false)
    private String checksum;

    @NotNull
    @Column(name = "user_code", nullable = false)
    private String userCode;

    @NotNull
    @Column(name = "component", nullable = false)
    private String component;

    @Column(name = "conditions", nullable = false)
    private String conditions;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExportStatusEnum status = ExportStatusEnum.PENDING;

    @Column(name = "url")
    private String url;

    @Column(name = "total")
    private Long total;

    @Column(name = "current")
    private Long current;

    @Column(name = "reason")
    private String reason;

    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = false;

    public ExportEntity() {
    }

    public ExportEntity(Long id, String checksum, String userCode, String component, String conditions, ExportStatusEnum status, String url, Long total, Long current, String reason) {
        this.id = id;
        this.checksum = checksum;
        this.userCode = userCode;
        this.component = component;
        this.conditions = conditions;
        this.status = status;
        this.url = url;
        this.total = total;
        this.current = current;
        this.reason = reason;
    }

    @Override
    public ExportEntity clone() {
        try {
            ExportEntity cloned = (ExportEntity) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone ExportEntity", e);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public ExportEntity checksum(String checksum) {
        setChecksum(checksum);
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getUserCode() {
        return userCode;
    }

    public ExportEntity userCode(String userCode) {
        setUserCode(userCode);
        return this;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getComponent() {
        return component;
    }

    public ExportEntity component(String component) {
        setComponent(component);
        return this;
    }
    public void setComponent(String component) {
        this.component = component;
    }

    public String getConditions() {
        return conditions;
    }
    public ExportEntity conditions(String conditions) {
        setConditions(conditions);
        return this;
    }
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getUrl() {
        return url;
    }
    public ExportEntity url(String url) {
        setUrl(url);
        return this;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTotal() {
        return total;
    }
    public ExportEntity total(Long total) {
        setTotal(total);
        return this;
    }
    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getCurrent() {
        return current;
    }
    public ExportEntity current(Long current) {
        setCurrent(current);
        return this;
    }
    public void setCurrent(Long current) {
        this.current = current;
    }

    public String getReason() {
        return reason;
    }
    public ExportEntity reason(String reason) {
        setReason(reason);
        return this;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public ExportStatusEnum getStatus() {
        return status;
    }
    public ExportEntity status(ExportStatusEnum status) {
        setStatus(status);
        return this;
    }
    public void setStatus(ExportStatusEnum status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}

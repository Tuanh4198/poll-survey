package io.yody.yosurvey.survey.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.nentangso.core.domain.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "nts_metafields")
@Where(clause = "deleted = false")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MetafieldEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "owner_resource", length = 20, nullable = false)
    @NotNull
    @Size(max = 20)
    private String ownerResource;

    @Column(name = "owner_id", nullable = false)
    @NotNull
    @Min(1L)
    private Long ownerId;

    @Column(name = "namespace", length = 20)
    @NotNull
    @Size(min = 2, max = 20)
    private String namespace;

    @Column(name = "nts_key", length = 30)
    @NotNull
    @Size(min = 3, max = 30)
    private String key;

    @Column(name = "nts_value", length = 65535)
    @Size(max = 65535)
    private String value;

    @Column(name = "nts_type", length = 50, nullable = false)
    @NotNull
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public MetafieldEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerResource() {
        return ownerResource;
    }

    public void setOwnerResource(String ownerResource) {
        this.ownerResource = ownerResource;
    }

    public MetafieldEntity ownerResource(String ownerResource) {
        setOwnerResource(ownerResource);
        return this;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public MetafieldEntity ownerId(Long ownerId) {
        setOwnerId(ownerId);
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public MetafieldEntity namespace(String namespace) {
        setNamespace(namespace);
        return this;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MetafieldEntity key(String key) {
        setKey(key);
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MetafieldEntity value(String value) {
        setValue(value);
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MetafieldEntity type(String type) {
        setType(type);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

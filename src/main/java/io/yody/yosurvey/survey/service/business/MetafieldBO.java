package io.yody.yosurvey.survey.service.business;

public class MetafieldBO {
    private Long id;
    private String key;
    private String value;
    private Long ownerId;
    private String ownerResource;
    private String namespace;
    private String type;
    private String description;

    public MetafieldBO(Long id, String key, String value, Long ownerId, String ownerResource, String namespace, String type, String description) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.ownerId = ownerId;
        this.ownerResource = ownerResource;
        this.namespace = namespace;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerResource() {
        return ownerResource;
    }

    public void setOwnerResource(String ownerResource) {
        this.ownerResource = ownerResource;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

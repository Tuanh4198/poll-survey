package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.domain.constant.MetafieldConstant;
import io.yody.yosurvey.survey.domain.enumeration.SurveyMetafieldEnum;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import org.nentangso.core.service.errors.NtsValidationException;

public class SurveyTemplateMetafieldBO {
    private Long id;
    private String key;
    private String value;
    private Long ownerId;
    private String ownerResource;
    private String namespace;
    private String type;
    private String description;

    public SurveyTemplateMetafieldBO(Long id, String key, String value, Long ownerId, String ownerResource, String namespace, String type, String description) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.ownerId = ownerId;
        this.ownerResource = ownerResource;
        this.namespace = namespace;
        this.type = type;
        this.description = description;
    }
    public SurveyTemplateMetafieldBO(SurveyTemplateAggregate root, MetafieldRequest request) {
        this.key = request.getKey();
        this.value = request.getValue();
        this.ownerId = root.getId();
        this.ownerResource = MetafieldConstant.SURVEY_TEMPLATE;
        this.namespace = MetafieldConstant.SURVEY_TEMPLATE;
        this.type = MetafieldConstant.SURVEY_TEMPLATE;
        this.description = "";
    }

    public SurveyTemplateMetafieldBO() {
    }

    public void validate() {
        if (SurveyMetafieldEnum.inValidKey(this.key)) {
            throw new NtsValidationException("message", String.format("Metafield không hợp lệ %s", this.getKey()));
        }
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
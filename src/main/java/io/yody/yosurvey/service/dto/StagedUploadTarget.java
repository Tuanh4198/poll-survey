package io.yody.yosurvey.service.dto;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class StagedUploadTarget implements Serializable {

    /**
     * Parameters of the media to be uploaded.
     */
    private Set<StagedUploadParameter> parameters = new LinkedHashSet<>();

    /**
     * The url to be passed as the original_source for the product create media mutation input.
     */
    private String resourceUrl;

    /**
     * Media URL.
     */
    private String url;

    public Set<StagedUploadParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Set<StagedUploadParameter> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(String name, String value) {
        parameters.add(new StagedUploadParameter(name, value));
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StagedUploadTarget{" +
            "parameters=" + parameters +
            ", resourceUrl='" + resourceUrl + '\'' +
            ", url='" + url + '\'' +
            '}';
    }
}

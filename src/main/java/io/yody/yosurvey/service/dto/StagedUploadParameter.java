package io.yody.yosurvey.service.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class StagedUploadParameter implements Serializable {

    /**
     * Parameter name.
     */
    @NotBlank
    private String name;

    /**
     * Parameter value.
     */
    @NotBlank
    private String value;

    public StagedUploadParameter() {}

    public StagedUploadParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StagedUploadParameter{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }
}

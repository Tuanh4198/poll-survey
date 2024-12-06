package io.yody.yosurvey.survey.web.rest.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class MetafieldRequest implements Serializable {
    private @NotNull @Size(
        min = 3,
        max = 30
    ) String key;
    private @Size(
        max = 65535
    ) String value;
    private @NotBlank @Size(
        max = 50
    ) String type;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }
}

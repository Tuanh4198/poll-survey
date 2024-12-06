package io.yody.yosurvey.survey.web.rest.request;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class SurveyExportRequest implements Serializable {
    @NotNull
    private String checksum;
    private String conditions;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}

package io.yody.yosurvey.survey.web.rest.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class SurveySubmitRequest implements Serializable {
    @NotNull
    @NotEmpty
    List<FieldSubmitRequest> fieldSubmits;

    public List<FieldSubmitRequest> getFieldSubmits() {
        return fieldSubmits;
    }

    public void setFieldSubmits(List<FieldSubmitRequest> fieldSubmits) {
        this.fieldSubmits = fieldSubmits;
    }
}

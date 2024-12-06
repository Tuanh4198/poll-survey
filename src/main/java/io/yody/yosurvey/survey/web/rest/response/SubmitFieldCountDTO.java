package io.yody.yosurvey.survey.web.rest.response;

import java.io.Serializable;
import java.util.Map;

public class SubmitFieldCountDTO implements Serializable {
    Map<Long, Long> fieldsCount;

    public SubmitFieldCountDTO(Map<Long, Long> fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    public Map<Long, Long> getFieldsCount() {
        return fieldsCount;
    }

    public void setFieldsCount(Map<Long, Long> fieldsCount) {
        this.fieldsCount = fieldsCount;
    }
}

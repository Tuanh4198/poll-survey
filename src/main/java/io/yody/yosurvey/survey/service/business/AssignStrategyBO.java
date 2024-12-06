package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class AssignStrategyBO {
    private Long id;
    private Long surveyId;
    List<AssignStrategyMetafieldBO> metafields;

    public AssignStrategyBO() {
    }

    public AssignStrategyBO(Long id, Long surveyId, List<AssignStrategyMetafieldBO> metafields) {
        this.id = id;
        this.surveyId = surveyId;
        this.metafields = metafields;
    }
    public AssignStrategyBO(SurveyAggregate survey, AssignStrategyRequest request) {
        this.id = request.getTempId();
        this.surveyId = survey.getId();
        _setMetafields(request.getMetafields());
    }

    private void _setMetafields(List<MetafieldRequest> requests) {
        this.metafields = new ArrayList<>();
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(metafieldRequest -> {
            this.metafields.add(new AssignStrategyMetafieldBO(this, metafieldRequest));
        });
    }
    private void validateMetafields() {
        for (AssignStrategyMetafieldBO metafieldBO : metafields) {
            metafieldBO.validate();
        }
    }

    public void update(SurveyAggregate survey, AssignStrategyRequest request) {
        this.id = request.getTempId();
        this.surveyId = survey.getId();
        _setMetafields(request.getMetafields());
    }
    public void validate() {
        validateMetafields();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public List<AssignStrategyMetafieldBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<AssignStrategyMetafieldBO> metafields) {
        this.metafields = metafields;
    }
}

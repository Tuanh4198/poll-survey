package io.yody.yosurvey.survey.service.business;

import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class AssignStrategyTemplateBO {
    private Long id;
    private Long surveyId;
    List<AssignStrategyTemplateMetafieldBO> metafields;

    public AssignStrategyTemplateBO(Long id, Long surveyId, List<AssignStrategyTemplateMetafieldBO> metafields) {
        this.id = id;
        this.surveyId = surveyId;
        this.metafields = metafields;
    }

    public AssignStrategyTemplateBO() {
    }

    public AssignStrategyTemplateBO(SurveyTemplateAggregate survey, AssignStrategyRequest request) {
        this.id = request.getTempId();
        this.surveyId = survey.getId();
        _setMetafields(request.getMetafields());
    }
    public void update(SurveyTemplateAggregate survey, AssignStrategyRequest request) {
        this.id = request.getTempId();
        this.surveyId = survey.getId();
        _setMetafields(request.getMetafields());
    }
    private void _setMetafields(List<MetafieldRequest> requests) {
        if (ObjectUtils.isEmpty(this.metafields)) {
            this.metafields = new ArrayList<>();
        }
        if (ObjectUtils.isEmpty(requests)) return;
        requests.forEach(metafieldRequest -> {
            this.metafields.add(new AssignStrategyTemplateMetafieldBO(this, metafieldRequest));
        });
    }
    private void validateMetafields() {
        for (AssignStrategyTemplateMetafieldBO metafieldBO : metafields) {
            metafieldBO.validate();
        }
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

    public List<AssignStrategyTemplateMetafieldBO> getMetafields() {
        return metafields;
    }

    public void setMetafields(List<AssignStrategyTemplateMetafieldBO> metafields) {
        this.metafields = metafields;
    }
}

package io.yody.yosurvey.survey.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveyTemplateMapperTest {

    private SurveyTemplateMapper surveyTemplateMapper;

    @BeforeEach
    public void setUp() {
        surveyTemplateMapper = new SurveyTemplateMapperImpl();
    }
}

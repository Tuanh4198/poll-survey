package io.yody.yosurvey.survey.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SurveySubmitMapperTest {

    private SurveySubmitMapper surveySubmitMapper;

    @BeforeEach
    public void setUp() {
        surveySubmitMapper = new SurveySubmitMapperImpl();
    }
}

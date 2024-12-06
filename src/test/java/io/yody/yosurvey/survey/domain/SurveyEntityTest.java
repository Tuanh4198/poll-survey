package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyEntity.class);
        SurveyEntity surveyEntity1 = new SurveyEntity();
        surveyEntity1.setId(1L);
        SurveyEntity surveyEntity2 = new SurveyEntity();
        surveyEntity2.setId(surveyEntity1.getId());
        assertThat(surveyEntity1).isEqualTo(surveyEntity2);
        surveyEntity2.setId(2L);
        assertThat(surveyEntity1).isNotEqualTo(surveyEntity2);
        surveyEntity1.setId(null);
        assertThat(surveyEntity1).isNotEqualTo(surveyEntity2);
    }
}

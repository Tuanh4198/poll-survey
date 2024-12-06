package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveySubmitEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveySubmitEntity.class);
        SurveySubmitEntity surveySubmitEntity1 = new SurveySubmitEntity();
        surveySubmitEntity1.setId(1L);
        SurveySubmitEntity surveySubmitEntity2 = new SurveySubmitEntity();
        surveySubmitEntity2.setId(surveySubmitEntity1.getId());
        assertThat(surveySubmitEntity1).isEqualTo(surveySubmitEntity2);
        surveySubmitEntity2.setId(2L);
        assertThat(surveySubmitEntity1).isNotEqualTo(surveySubmitEntity2);
        surveySubmitEntity1.setId(null);
        assertThat(surveySubmitEntity1).isNotEqualTo(surveySubmitEntity2);
    }
}

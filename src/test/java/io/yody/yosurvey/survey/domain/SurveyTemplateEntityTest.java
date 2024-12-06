package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyTemplateEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyTemplateEntity.class);
        SurveyTemplateEntity surveyTemplateEntity1 = new SurveyTemplateEntity();
        surveyTemplateEntity1.setId(1L);
        SurveyTemplateEntity surveyTemplateEntity2 = new SurveyTemplateEntity();
        surveyTemplateEntity2.setId(surveyTemplateEntity1.getId());
        assertThat(surveyTemplateEntity1).isEqualTo(surveyTemplateEntity2);
        surveyTemplateEntity2.setId(2L);
        assertThat(surveyTemplateEntity1).isNotEqualTo(surveyTemplateEntity2);
        surveyTemplateEntity1.setId(null);
        assertThat(surveyTemplateEntity1).isNotEqualTo(surveyTemplateEntity2);
    }
}

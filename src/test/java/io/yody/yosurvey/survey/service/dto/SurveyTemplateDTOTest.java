package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveyTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveyTemplateDTO.class);
        SurveyTemplateDTO surveyTemplateDTO1 = new SurveyTemplateDTO();
        surveyTemplateDTO1.setId(1L);
        SurveyTemplateDTO surveyTemplateDTO2 = new SurveyTemplateDTO();
        assertThat(surveyTemplateDTO1).isNotEqualTo(surveyTemplateDTO2);
        surveyTemplateDTO2.setId(surveyTemplateDTO1.getId());
        assertThat(surveyTemplateDTO1).isEqualTo(surveyTemplateDTO2);
        surveyTemplateDTO2.setId(2L);
        assertThat(surveyTemplateDTO1).isNotEqualTo(surveyTemplateDTO2);
        surveyTemplateDTO1.setId(null);
        assertThat(surveyTemplateDTO1).isNotEqualTo(surveyTemplateDTO2);
    }
}

package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SurveySubmitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SurveySubmitDTO.class);
        SurveySubmitDTO surveySubmitDTO1 = new SurveySubmitDTO();
        surveySubmitDTO1.setId(1L);
        SurveySubmitDTO surveySubmitDTO2 = new SurveySubmitDTO();
        assertThat(surveySubmitDTO1).isNotEqualTo(surveySubmitDTO2);
        surveySubmitDTO2.setId(surveySubmitDTO1.getId());
        assertThat(surveySubmitDTO1).isEqualTo(surveySubmitDTO2);
        surveySubmitDTO2.setId(2L);
        assertThat(surveySubmitDTO1).isNotEqualTo(surveySubmitDTO2);
        surveySubmitDTO1.setId(null);
        assertThat(surveySubmitDTO1).isNotEqualTo(surveySubmitDTO2);
    }
}

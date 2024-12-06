package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmployeeSurveyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmployeeSurveyDTO.class);
        EmployeeSurveyDTO employeeSurveyDTO1 = new EmployeeSurveyDTO();
        employeeSurveyDTO1.setId(1L);
        EmployeeSurveyDTO employeeSurveyDTO2 = new EmployeeSurveyDTO();
        assertThat(employeeSurveyDTO1).isNotEqualTo(employeeSurveyDTO2);
        employeeSurveyDTO2.setId(employeeSurveyDTO1.getId());
        assertThat(employeeSurveyDTO1).isEqualTo(employeeSurveyDTO2);
        employeeSurveyDTO2.setId(2L);
        assertThat(employeeSurveyDTO1).isNotEqualTo(employeeSurveyDTO2);
        employeeSurveyDTO1.setId(null);
        assertThat(employeeSurveyDTO1).isNotEqualTo(employeeSurveyDTO2);
    }
}

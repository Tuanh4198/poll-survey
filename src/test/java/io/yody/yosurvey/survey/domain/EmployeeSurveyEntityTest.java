package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmployeeSurveyEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmployeeSurveyEntity.class);
        EmployeeSurveyEntity employeeSurveyEntity1 = new EmployeeSurveyEntity();
        employeeSurveyEntity1.setId(1L);
        EmployeeSurveyEntity employeeSurveyEntity2 = new EmployeeSurveyEntity();
        employeeSurveyEntity2.setId(employeeSurveyEntity1.getId());
        assertThat(employeeSurveyEntity1).isEqualTo(employeeSurveyEntity2);
        employeeSurveyEntity2.setId(2L);
        assertThat(employeeSurveyEntity1).isNotEqualTo(employeeSurveyEntity2);
        employeeSurveyEntity1.setId(null);
        assertThat(employeeSurveyEntity1).isNotEqualTo(employeeSurveyEntity2);
    }
}

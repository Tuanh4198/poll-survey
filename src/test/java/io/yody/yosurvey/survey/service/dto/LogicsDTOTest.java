package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogicsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LogicsDTO.class);
        LogicsDTO logicsDTO1 = new LogicsDTO();
        logicsDTO1.setId(1L);
        LogicsDTO logicsDTO2 = new LogicsDTO();
        assertThat(logicsDTO1).isNotEqualTo(logicsDTO2);
        logicsDTO2.setId(logicsDTO1.getId());
        assertThat(logicsDTO1).isEqualTo(logicsDTO2);
        logicsDTO2.setId(2L);
        assertThat(logicsDTO1).isNotEqualTo(logicsDTO2);
        logicsDTO1.setId(null);
        assertThat(logicsDTO1).isNotEqualTo(logicsDTO2);
    }
}

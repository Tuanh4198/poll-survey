package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignStrategyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignStrategyDTO.class);
        AssignStrategyDTO assignStrategyDTO1 = new AssignStrategyDTO();
        assignStrategyDTO1.setId(1L);
        AssignStrategyDTO assignStrategyDTO2 = new AssignStrategyDTO();
        assertThat(assignStrategyDTO1).isNotEqualTo(assignStrategyDTO2);
        assignStrategyDTO2.setId(assignStrategyDTO1.getId());
        assertThat(assignStrategyDTO1).isEqualTo(assignStrategyDTO2);
        assignStrategyDTO2.setId(2L);
        assertThat(assignStrategyDTO1).isNotEqualTo(assignStrategyDTO2);
        assignStrategyDTO1.setId(null);
        assertThat(assignStrategyDTO1).isNotEqualTo(assignStrategyDTO2);
    }
}

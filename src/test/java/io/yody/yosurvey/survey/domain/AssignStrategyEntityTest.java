package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssignStrategyEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssignStrategyEntity.class);
        AssignStrategyEntity assignStrategyEntity1 = new AssignStrategyEntity();
        assignStrategyEntity1.setId(1L);
        AssignStrategyEntity assignStrategyEntity2 = new AssignStrategyEntity();
        assignStrategyEntity2.setId(assignStrategyEntity1.getId());
        assertThat(assignStrategyEntity1).isEqualTo(assignStrategyEntity2);
        assignStrategyEntity2.setId(2L);
        assertThat(assignStrategyEntity1).isNotEqualTo(assignStrategyEntity2);
        assignStrategyEntity1.setId(null);
        assertThat(assignStrategyEntity1).isNotEqualTo(assignStrategyEntity2);
    }
}

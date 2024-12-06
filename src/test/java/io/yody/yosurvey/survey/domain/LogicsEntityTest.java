package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LogicsEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LogicsEntity.class);
        LogicsEntity logicsEntity1 = new LogicsEntity();
        logicsEntity1.setId(1L);
        LogicsEntity logicsEntity2 = new LogicsEntity();
        logicsEntity2.setId(logicsEntity1.getId());
        assertThat(logicsEntity1).isEqualTo(logicsEntity2);
        logicsEntity2.setId(2L);
        assertThat(logicsEntity1).isNotEqualTo(logicsEntity2);
        logicsEntity1.setId(null);
        assertThat(logicsEntity1).isNotEqualTo(logicsEntity2);
    }
}

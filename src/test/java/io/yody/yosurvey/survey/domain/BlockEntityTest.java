package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockEntity.class);
        BlockEntity blockEntity1 = new BlockEntity();
        blockEntity1.setId(1L);
        BlockEntity blockEntity2 = new BlockEntity();
        blockEntity2.setId(blockEntity1.getId());
        assertThat(blockEntity1).isEqualTo(blockEntity2);
        blockEntity2.setId(2L);
        assertThat(blockEntity1).isNotEqualTo(blockEntity2);
        blockEntity1.setId(null);
        assertThat(blockEntity1).isNotEqualTo(blockEntity2);
    }
}

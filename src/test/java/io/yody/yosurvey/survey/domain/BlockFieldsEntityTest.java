package io.yody.yosurvey.survey.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockFieldsEntityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockFieldsEntity.class);
        BlockFieldsEntity blockFieldsEntity1 = new BlockFieldsEntity();
        blockFieldsEntity1.setId(1L);
        BlockFieldsEntity blockFieldsEntity2 = new BlockFieldsEntity();
        blockFieldsEntity2.setId(blockFieldsEntity1.getId());
        assertThat(blockFieldsEntity1).isEqualTo(blockFieldsEntity2);
        blockFieldsEntity2.setId(2L);
        assertThat(blockFieldsEntity1).isNotEqualTo(blockFieldsEntity2);
        blockFieldsEntity1.setId(null);
        assertThat(blockFieldsEntity1).isNotEqualTo(blockFieldsEntity2);
    }
}

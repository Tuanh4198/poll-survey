package io.yody.yosurvey.survey.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import io.yody.yosurvey.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BlockFieldsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockFieldsDTO.class);
        BlockFieldsDTO blockFieldsDTO1 = new BlockFieldsDTO();
        blockFieldsDTO1.setId(1L);
        BlockFieldsDTO blockFieldsDTO2 = new BlockFieldsDTO();
        assertThat(blockFieldsDTO1).isNotEqualTo(blockFieldsDTO2);
        blockFieldsDTO2.setId(blockFieldsDTO1.getId());
        assertThat(blockFieldsDTO1).isEqualTo(blockFieldsDTO2);
        blockFieldsDTO2.setId(2L);
        assertThat(blockFieldsDTO1).isNotEqualTo(blockFieldsDTO2);
        blockFieldsDTO1.setId(null);
        assertThat(blockFieldsDTO1).isNotEqualTo(blockFieldsDTO2);
    }
}

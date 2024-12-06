package io.yody.yosurvey.survey.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockFieldsMapperTest {

    private BlockFieldsMapper blockFieldsMapper;

    @BeforeEach
    public void setUp() {
        blockFieldsMapper = new BlockFieldsMapperImpl();
    }
}

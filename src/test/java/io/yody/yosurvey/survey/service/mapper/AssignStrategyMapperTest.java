package io.yody.yosurvey.survey.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssignStrategyMapperTest {

    private AssignStrategyMapper assignStrategyMapper;

    @BeforeEach
    public void setUp() {
        assignStrategyMapper = new AssignStrategyMapperImpl();
    }
}

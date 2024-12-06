package io.yody.yosurvey.survey.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogicsMapperTest {

    private LogicsMapper logicsMapper;

    @BeforeEach
    public void setUp() {
        logicsMapper = new LogicsMapperImpl();
    }
}

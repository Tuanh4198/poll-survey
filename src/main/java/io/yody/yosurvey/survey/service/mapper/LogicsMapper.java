package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.LogicsEntity;
import io.yody.yosurvey.survey.service.dto.LogicsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LogicsEntity} and its DTO {@link LogicsDTO}.
 */
@Mapper(componentModel = "spring")
public interface LogicsMapper extends EntityMapper<LogicsDTO, LogicsEntity> {}

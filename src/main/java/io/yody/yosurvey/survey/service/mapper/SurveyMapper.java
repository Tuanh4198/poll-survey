package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for the entity {@link SurveyEntity} and its DTO {@link SurveyDTO}.
 */
@Mapper(componentModel = "spring", uses = { BaseMapper.class, BlockMapper.class, BlockFieldsMapper.class, AssignStrategyMapper.class })
public interface SurveyMapper extends EntityMapper<SurveyDTO, SurveyEntity> {
    SurveyMapper INSTANCE = Mappers.getMapper(SurveyMapper.class);
    SurveyEntity aggregateToEntity(SurveyAggregate aggregate);
    @Mapping(source = "assignStrategies", target = "assignStrategies")
    @Mapping(source = "blocks", target = "blocks")
    SurveyDTO toDto(SurveyEntity entity);

    SurveyAggregate toAggregate(SurveyEntity entity);
}

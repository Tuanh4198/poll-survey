package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.business.SurveyTemplateAggregate;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity {@link SurveyTemplateEntity} and its DTO {@link SurveyTemplateDTO}.
 */
@Mapper(componentModel = "spring", uses = { BaseTemplateMapper.class, BlockTemplateMapper.class, BlockFieldsTemplateMapper.class,
    AssignStrategyTemplateMapper.class })
public interface SurveyTemplateMapper extends EntityMapper<SurveyTemplateDTO, SurveyTemplateEntity> {
    SurveyMapper INSTANCE = Mappers.getMapper(SurveyMapper.class);
    SurveyTemplateEntity aggregateToEntity(SurveyTemplateAggregate aggregate);
    @Mapping(source = "assignStrategies", target = "assignStrategies")
    @Mapping(source = "blocks", target = "blocks")
    SurveyTemplateDTO toDto(SurveyTemplateEntity entity);

    SurveyTemplateAggregate toAggregate(SurveyTemplateEntity entity);
}

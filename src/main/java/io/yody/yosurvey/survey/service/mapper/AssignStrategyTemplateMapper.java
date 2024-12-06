package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.AssignStrategyTemplateEntity;
import io.yody.yosurvey.survey.service.business.AssignStrategyTemplateBO;
import io.yody.yosurvey.survey.service.dto.AssignStrategyTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BaseTemplateMapper.class })
public interface AssignStrategyTemplateMapper {
    @Mapping(source = "surveyId", target = "survey", qualifiedByName = "mapToSurveyTemplate")
    AssignStrategyTemplateEntity boToEntityTemplate(AssignStrategyTemplateBO bo);

    @Mapping(target = "survey.assignStrategies", ignore = true)
    @Mapping(target = "survey.blocks", ignore = true)
    AssignStrategyTemplateDTO toDtoTemplate(AssignStrategyTemplateEntity entity);
}

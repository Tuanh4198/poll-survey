package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockTemplateEntity;
import io.yody.yosurvey.survey.service.business.BlockTemplateBO;
import io.yody.yosurvey.survey.service.dto.BlockTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BaseTemplateMapper.class, BlockFieldsTemplateMapper.class })
public interface BlockTemplateMapper {
    @Mapping(source = "surveyId", target = "survey", qualifiedByName = "mapToSurveyTemplate")
    BlockTemplateEntity boToEntityTemplate(BlockTemplateBO bo);
    @Mapping(target = "survey.assignStrategies", ignore = true)
    @Mapping(target = "survey.blocks", ignore = true)
    BlockTemplateDTO toDtoTemplate(BlockTemplateEntity entity);
}

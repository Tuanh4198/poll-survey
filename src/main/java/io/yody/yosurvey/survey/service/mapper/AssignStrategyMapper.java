package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.AssignStrategyEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.service.business.AssignStrategyBO;
import io.yody.yosurvey.survey.service.dto.AssignStrategyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link AssignStrategyEntity} and its DTO {@link AssignStrategyDTO}.
 */
@Mapper(componentModel = "spring", uses = { BaseMapper.class })
public interface AssignStrategyMapper extends EntityMapper<AssignStrategyDTO, AssignStrategyEntity> {
    @Mapping(source = "surveyId", target = "survey", qualifiedByName = "mapToSurvey")
    AssignStrategyEntity boToEntity(AssignStrategyBO bo);

    @Mapping(target = "survey.assignStrategies", ignore = true)
    @Mapping(target = "survey.blocks", ignore = true)
    AssignStrategyDTO toDto(AssignStrategyEntity entity);
}

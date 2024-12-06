package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.AssignStrategyEntity;
import io.yody.yosurvey.survey.domain.BlockEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.service.business.BlockBO;
import io.yody.yosurvey.survey.service.dto.AssignStrategyDTO;
import io.yody.yosurvey.survey.service.dto.BlockDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link BlockEntity} and its DTO {@link BlockDTO}.
 */
@Mapper(componentModel = "spring", uses = { BaseMapper.class, BlockFieldsMapper.class })
public interface BlockMapper extends EntityMapper<BlockDTO, BlockEntity> {
    @Mapping(source = "surveyId", target = "survey", qualifiedByName = "mapToSurvey")
    BlockEntity boToEntity(BlockBO bo);
    @Mapping(target = "survey.assignStrategies", ignore = true)
    @Mapping(target = "survey.blocks", ignore = true)
    BlockDTO toDto(BlockEntity entity);
}

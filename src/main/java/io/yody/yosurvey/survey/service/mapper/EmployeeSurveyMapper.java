package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for the entity {@link EmployeeSurveyEntity} and its DTO {@link EmployeeSurveyDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeSurveyMapper extends EntityMapper<EmployeeSurveyDTO, EmployeeSurveyEntity> {
    EmployeeSurveyMapper INSTANCE = Mappers.getMapper(EmployeeSurveyMapper.class);
}

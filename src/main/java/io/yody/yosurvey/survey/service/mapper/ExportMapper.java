package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.ExportEntity;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.dto.ExportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExportMapper extends EntityMapper<ExportDTO, ExportEntity>{
    ExportMapper INSTANCE = Mappers.getMapper(ExportMapper.class);
}

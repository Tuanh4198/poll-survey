package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockEntity;
import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.service.dto.BlockDTO;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveySubmitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SurveySubmitEntity} and its DTO {@link SurveySubmitDTO}.
 */
@Mapper(componentModel = "spring")
public interface SurveySubmitMapper extends EntityMapper<SurveySubmitDTO, SurveySubmitEntity> {
}

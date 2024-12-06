package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockEntity;
import io.yody.yosurvey.survey.domain.BlockTemplateEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BaseMapper {
    @Named("mapToSurvey")
    default SurveyEntity mapToSurvey(Long id) {
        if (id == null) {
            return null;
        }
        return new SurveyEntity(id);
    }
    @Named("mapToBlock")
    default BlockEntity mapToBlock(Long id) {
        if (id == null) {
            return null;
        }
        return new BlockEntity(id);
    }
}



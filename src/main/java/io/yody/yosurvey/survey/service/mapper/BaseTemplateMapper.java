package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockTemplateEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BaseTemplateMapper {
    @Named("mapToBlockTemplate")
    default BlockTemplateEntity mapToBlockTemplate(Long id) {
        if (id == null) {
            return null;
        }
        return new BlockTemplateEntity(id);
    }

    @Named("mapToSurveyTemplate")
    default SurveyTemplateEntity mapToSurveyTemplate(Long id) {
        if (id == null) {
            return null;
        }
        return new SurveyTemplateEntity(id);
    }
}

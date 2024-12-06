package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.MetafieldEntity;
import io.yody.yosurvey.survey.service.business.*;
import io.yody.yosurvey.survey.service.dto.MetafieldDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetafieldMapper extends EntityMapper<MetafieldDTO, MetafieldEntity> {
    MetafieldMapper INSTANCE = Mappers.getMapper(MetafieldMapper.class);
    List<SurveyMetafieldBO> toSurveyBO(List<MetafieldEntity> metafields);
    List<BlockMetafieldBO> toBlockBO(List<MetafieldEntity> metafields);
    List<BlockFieldsMetafieldBO> toBlockFieldsBO(List<MetafieldEntity> metafields);
    List<AssignStrategyMetafieldBO> toAssignStrategyBO(List<MetafieldEntity> metafields);
    List<SurveyTemplateMetafieldBO> toSurveyTemplateBO(List<MetafieldEntity> metafields);
    List<BlockMetafieldTemplateBO> toBlockTemplateBO(List<MetafieldEntity> metafields);
    List<BlockFieldsTemplateMetafieldBO> toBlockFieldsTemplateBO(List<MetafieldEntity> metafields);
    List<AssignStrategyTemplateMetafieldBO> toAssignStrategyTemplateBO(List<MetafieldEntity> metafields);
    List<MetafieldEntity> surveyMetafieldBosToEntity(List<SurveyMetafieldBO> metafieldBOS);
    List<MetafieldEntity> blockMetafieldBosToEntity(List<BlockMetafieldBO> metafieldBOS);
    List<MetafieldEntity> blockFieldMetafieldBosToEntity(List<BlockFieldsMetafieldBO> metafieldBOS);
    List<MetafieldEntity> assignStrategyMetafieldBosToEntity(List<AssignStrategyMetafieldBO> metafieldBOS);
    List<MetafieldEntity> surveyMetafieldTemplateBosToEntity(List<SurveyTemplateMetafieldBO> metafieldBOS);
    List<MetafieldEntity> blockMetafieldTemplateBosToEntity(List<BlockMetafieldTemplateBO> metafieldBOS);
    List<MetafieldEntity> blockFieldMetafieldTemplateBosToEntity(List<BlockFieldsTemplateMetafieldBO> metafieldBOS);
    List<MetafieldEntity> assignStrategyMetafieldTemplateBosToEntity(List<AssignStrategyTemplateMetafieldBO> metafieldBOS);
}

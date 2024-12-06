package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockFieldsTemplateEntity;
import io.yody.yosurvey.survey.domain.BlockTemplateEntity;
import io.yody.yosurvey.survey.service.business.BlockFieldsTemplateBO;
import io.yody.yosurvey.survey.service.dto.BlockFieldsTemplateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = { BaseTemplateMapper.class })
public interface BlockFieldsTemplateMapper {
    @Mapping(source = "blockId", target = "block", qualifiedByName = "mapToBlockTemplate")
    BlockFieldsTemplateEntity boToEntityTemplate(BlockFieldsTemplateBO bo);
    @Mapping(target = "block.blockFields", ignore = true)
    @Mapping(target = "block.survey", ignore = true)
    BlockFieldsTemplateDTO toDtoTemplate(BlockFieldsTemplateEntity entity);
}

package io.yody.yosurvey.survey.service.mapper;

import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import io.yody.yosurvey.survey.service.business.BlockFieldsBO;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link BlockFieldsEntity} and its DTO {@link BlockFieldsDTO}.
 */
@Mapper(componentModel = "spring", uses = BaseMapper.class )
public interface BlockFieldsMapper extends EntityMapper<BlockFieldsDTO, BlockFieldsEntity> {
    @Mapping(source = "blockId", target = "block", qualifiedByName = "mapToBlock")
    BlockFieldsEntity boToEntity(BlockFieldsBO bo);
    @Mapping(target = "block.blockFields", ignore = true)
    @Mapping(target = "block.survey", ignore = true)
    BlockFieldsDTO toDto(BlockFieldsEntity entity);
}

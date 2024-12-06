package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.survey.domain.MetafieldEntity;
import io.yody.yosurvey.survey.repository.MetafieldRepository;
import io.yody.yosurvey.survey.service.business.*;
import io.yody.yosurvey.survey.service.dto.*;
import io.yody.yosurvey.survey.service.mapper.MetafieldMapper;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.yody.yosurvey.survey.domain.constant.MetafieldConstant.*;

@Component
public class MetafieldHelper {
    private static final Logger log = LoggerFactory.getLogger(MetafieldHelper.class);
    private final MetafieldRepository metafieldRepository;
    public MetafieldHelper(MetafieldRepository metafieldRepository) {
        this.metafieldRepository = metafieldRepository;
    }
    public List<MetafieldEntity> getMetafields(String ownerResource, Long ownerId) {
        return metafieldRepository.findAllByOwnerResourceAndOwnerId(ownerResource, ownerId);
    }
    public void deleteAll(List<MetafieldEntity> metafields) {
        List<Long> ids = metafields.stream().map(MetafieldEntity::getId).collect(Collectors.toList());
        metafieldRepository.deleteAllById(ids);
    }
    public List<MetafieldEntity> saveAll(List<MetafieldEntity> metafields) {
        metafields = metafieldRepository.saveAll(metafields);
        return metafields;
    }

    public static List<MetafieldEntity> getAllMetafields(SurveyAggregate aggregate) {
        List<MetafieldEntity> metafields = new ArrayList<>();

        List<SurveyMetafieldBO> surveyMetafieldBOS = NestedHelper.getChilds(aggregate::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.surveyMetafieldBosToEntity(surveyMetafieldBOS));

        List<BlockMetafieldBO> blockMetafieldBOS =
            NestedHelper.getNestedChilds(aggregate::getBlocks, BlockBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.blockMetafieldBosToEntity(blockMetafieldBOS));

        List<BlockFieldsMetafieldBO> blockFieldsMetafieldBOS = NestedHelper.getNestedChilds2(
            aggregate::getBlocks, BlockBO::getBlockFields, BlockFieldsBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.blockFieldMetafieldBosToEntity(blockFieldsMetafieldBOS));

        List<AssignStrategyMetafieldBO> assignStrategyMetafieldBOS =
            NestedHelper.getNestedChilds(aggregate::getAssignStrategies, AssignStrategyBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.assignStrategyMetafieldBosToEntity(assignStrategyMetafieldBOS));

        return metafields;
    }

    public static List<MetafieldEntity> getAllMetafields(SurveyTemplateAggregate aggregate) {
        List<MetafieldEntity> metafields = new ArrayList<>();

        List<SurveyTemplateMetafieldBO> surveyMetafieldBOS = NestedHelper.getChilds(aggregate::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.surveyMetafieldTemplateBosToEntity(surveyMetafieldBOS));

        List<BlockMetafieldTemplateBO> blockMetafieldBOS = NestedHelper
            .getNestedChilds(aggregate::getBlocks, BlockTemplateBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.blockMetafieldTemplateBosToEntity(blockMetafieldBOS));

        List<BlockFieldsTemplateMetafieldBO> blockFieldsMetafieldBOS = NestedHelper
            .getNestedChilds2(aggregate::getBlocks, BlockTemplateBO::getBlockFields, BlockFieldsTemplateBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.blockFieldMetafieldTemplateBosToEntity(blockFieldsMetafieldBOS));

        List<AssignStrategyTemplateMetafieldBO> assignStrategyMetafieldBOS = NestedHelper
            .getNestedChilds(aggregate::getAssignStrategies, AssignStrategyTemplateBO::getMetafields);
        metafields.addAll(MetafieldMapper.INSTANCE.assignStrategyMetafieldTemplateBosToEntity(assignStrategyMetafieldBOS));

        return metafields;
    }

    private <MTYPE> Map<Long, List<MTYPE>> convertToMap(
        List<MetafieldEntity> metafields,
        Function<List<MetafieldEntity>, List<MTYPE>> mapperFunction,
        Function<MTYPE, Long> getOwnerIdFunction
    ) {
        List<MTYPE> metafieldDTOS = mapperFunction.apply(metafields);
        Map<Long, List<MTYPE>> metafieldMap = metafieldDTOS.stream()
            .collect(Collectors.groupingBy(getOwnerIdFunction));
        return metafieldMap;
    }
    private <DTO, MTYPE> void enrichMetafields(List<DTO> dtos, Function<DTO, Long> getIdFunction,
                                               BiConsumer<DTO, List<MTYPE>> setMetafieldFunction,
                                               Function<List<MetafieldEntity>, List<MTYPE>> metafieldMapperFunction,
                                               Function<MTYPE, Long> metafieldGetOwnerIdFunction,
                                               String metafieldEnum) {
        List<Long> ids = dtos.stream().map(getIdFunction).collect(Collectors.toList());
        List<MetafieldEntity> metafields = metafieldRepository.findAllByOwnerResourceAndOwnerIdIn(metafieldEnum, ids);
        Map<Long, List<MTYPE>> metafieldMap = convertToMap(metafields, metafieldMapperFunction, metafieldGetOwnerIdFunction);
        for (DTO dto : dtos) {
            setMetafieldFunction.accept(dto, metafieldMap.get(getIdFunction.apply(dto)));
        }
    }

    public void enrichMetafieldSurvey(List<SurveyDTO> dtos) {
        enrichMetafields(dtos, SurveyDTO::getId, SurveyDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId , SURVEY);
    }

    public void enrichMetafieldBlocks(List<BlockDTO> dtos) {
        enrichMetafields(dtos, BlockDTO::getId, BlockDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, BLOCK);
    }

    public void enrichMetafieldBlockFields(List<BlockFieldsDTO> dtos) {
        enrichMetafields(dtos, BlockFieldsDTO::getId, BlockFieldsDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, BLOCK_FIELDS);
    }

    public void enrichMetafieldAssignStrategy(List<AssignStrategyDTO> dtos) {
        enrichMetafields(dtos, AssignStrategyDTO::getId, AssignStrategyDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, ASSIGN_STRATEGY);
    }

    public void enrichMetafieldSurveyTemplate(List<SurveyTemplateDTO> dtos) {
        enrichMetafields(dtos, SurveyTemplateDTO::getId, SurveyTemplateDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, SURVEY_TEMPLATE);
    }

    public void enrichMetafieldBlocksTemplate(List<BlockTemplateDTO> dtos) {
        enrichMetafields(dtos, BlockTemplateDTO::getId, BlockTemplateDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, BLOCK_TEMPLATE);
    }

    public void enrichMetafieldBlockFieldsTemplate(List<BlockFieldsTemplateDTO> dtos) {
        enrichMetafields(dtos, BlockFieldsTemplateDTO::getId, BlockFieldsTemplateDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, BLOCK_FIELDS_TEMPLATE);
    }

    public void enrichMetafieldAssignStrategyTemplate(List<AssignStrategyTemplateDTO> dtos) {
        enrichMetafields(dtos, AssignStrategyTemplateDTO::getId, AssignStrategyTemplateDTO::setMetafields,
            MetafieldMapper.INSTANCE::toDto, MetafieldDTO::getOwnerId, ASSIGN_STRATEGY_TEMPLATE);
    }

    public void enrichMetafieldsSurveyAggregate(List<SurveyAggregate> aggregates) {
        enrichMetafields(aggregates, SurveyAggregate::getId, SurveyAggregate::setMetafields,
            MetafieldMapper.INSTANCE::toSurveyBO, SurveyMetafieldBO::getOwnerId, SURVEY);
    }

    public void enrichMetafieldsBlockBO(List<BlockBO> bos) {
        enrichMetafields(bos, BlockBO::getId, BlockBO::setMetafields,
            MetafieldMapper.INSTANCE::toBlockBO, BlockMetafieldBO::getOwnerId, BLOCK);
    }

    public void enrichMetafieldsBlockFieldsBO(List<BlockFieldsBO> bos) {
        enrichMetafields(bos, BlockFieldsBO::getId, BlockFieldsBO::setMetafields,
            MetafieldMapper.INSTANCE::toBlockFieldsBO, BlockFieldsMetafieldBO::getOwnerId, BLOCK_FIELDS);
    }

    public void enrichMetafieldsAssignStrategyBO(List<AssignStrategyBO> bos) {
        enrichMetafields(bos, AssignStrategyBO::getId, AssignStrategyBO::setMetafields,
            MetafieldMapper.INSTANCE::toAssignStrategyBO, AssignStrategyMetafieldBO::getOwnerId, ASSIGN_STRATEGY);
    }

    public void enrichMetafieldsSurveyTemplateAggregate(List<SurveyTemplateAggregate> aggregates) {
        enrichMetafields(aggregates, SurveyTemplateAggregate::getId, SurveyTemplateAggregate::setMetafields,
            MetafieldMapper.INSTANCE::toSurveyTemplateBO, SurveyTemplateMetafieldBO::getOwnerId, SURVEY_TEMPLATE);
    }

    public void enrichMetafieldsBlockTemplateBO(List<BlockTemplateBO> bos) {
        enrichMetafields(bos, BlockTemplateBO::getId, BlockTemplateBO::setMetafields,
            MetafieldMapper.INSTANCE::toBlockTemplateBO, BlockMetafieldTemplateBO::getOwnerId, BLOCK_TEMPLATE);
    }

    public void enrichMetafieldsBlockFieldsTemplateBO(List<BlockFieldsTemplateBO> bos) {
        enrichMetafields(bos, BlockFieldsTemplateBO::getId, BlockFieldsTemplateBO::setMetafields,
            MetafieldMapper.INSTANCE::toBlockFieldsTemplateBO, BlockFieldsTemplateMetafieldBO::getOwnerId, BLOCK_FIELDS_TEMPLATE);
    }

    public void enrichMetafieldsAssignStrategyTemplateBO(List<AssignStrategyTemplateBO> bos) {
        enrichMetafields(bos, AssignStrategyTemplateBO::getId, AssignStrategyTemplateBO::setMetafields,
            MetafieldMapper.INSTANCE::toAssignStrategyTemplateBO, AssignStrategyTemplateMetafieldBO::getOwnerId,ASSIGN_STRATEGY_TEMPLATE);
    }
}

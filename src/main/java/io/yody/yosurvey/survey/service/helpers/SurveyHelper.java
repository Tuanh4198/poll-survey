package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.service.AwsS3MediaProvider;
import io.yody.yosurvey.survey.service.business.*;
import io.yody.yosurvey.survey.service.dto.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SurveyHelper {
    public static List<BlockBO> getBlockBos(SurveyAggregate aggregate) {
        List<BlockBO> blocks = NestedHelper.getChilds(aggregate::getBlocks);
        return blocks;
    }
    public static List<BlockFieldsBO> getBlockFieldBos(SurveyAggregate aggregate) {
        List<BlockFieldsBO> blockFields = NestedHelper.getNestedChilds(aggregate::getBlocks, BlockBO::getBlockFields);
        return blockFields;
    }
    public static List<AssignStrategyBO> getAssignStrategyBos(SurveyAggregate aggregate) {
        List<AssignStrategyBO> assignStrategies = NestedHelper.getChilds(aggregate::getAssignStrategies);
        return assignStrategies;
    }
    public static List<BlockDTO> getBlockDtos(SurveyDTO survey) {
        List<BlockDTO> blocks = NestedHelper.getChilds(survey::getBlocks);
        return blocks;
    }
    public static List<BlockFieldsDTO> getBlockFieldDtos(SurveyDTO survey) {
        List<BlockFieldsDTO> blockFields = NestedHelper.getNestedChilds(survey::getBlocks, BlockDTO::getBlockFields);
        return blockFields;
    }
    public static List<AssignStrategyDTO> getAssignStrategyDtos(SurveyDTO survey) {
        List<AssignStrategyDTO> assignStrategies = NestedHelper.getChilds(survey::getAssignStrategies);
        return assignStrategies;
    }

    public static List<BlockTemplateBO> getBlockTemplateBos(SurveyTemplateAggregate aggregate) {
        List<BlockTemplateBO> blocks = NestedHelper.getChilds(aggregate::getBlocks);
        return blocks;
    }
    public static List<BlockFieldsTemplateBO> getBlockFieldBos(SurveyTemplateAggregate aggregate) {
        List<BlockFieldsTemplateBO> blockFields = NestedHelper.getNestedChilds(aggregate::getBlocks, BlockTemplateBO::getBlockFields);
        return blockFields;
    }
    public static List<AssignStrategyTemplateBO> getAssignStrategyTemplateBos(SurveyTemplateAggregate aggregate) {
        List<AssignStrategyTemplateBO> assignStrategies = NestedHelper.getChilds(aggregate::getAssignStrategies);
        return assignStrategies;
    }

    public static List<BlockTemplateDTO> getBlockTemplateDtos(SurveyTemplateDTO survey) {
        List<BlockTemplateDTO> blocks = NestedHelper.getChilds(survey::getBlocks);
        return blocks;
    }
    public static List<BlockFieldsTemplateDTO> getBlockFieldTemplateDtos(SurveyTemplateDTO survey) {
        List<BlockFieldsTemplateDTO> blockFields = NestedHelper.getNestedChilds(survey::getBlocks, BlockTemplateDTO::getBlockFields);
        return blockFields;
    }
    public static List<AssignStrategyTemplateDTO> getAssignStrategyTemplateDtos(SurveyTemplateDTO survey) {
        List<AssignStrategyTemplateDTO> assignStrategies = NestedHelper.getChilds(survey::getAssignStrategies);
        return assignStrategies;
    }
}

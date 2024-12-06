package io.yody.yosurvey.survey.service.jobs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.survey.service.SurveySubmitQueryService;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveyCriteria;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveySubmitCriteria;
import io.yody.yosurvey.survey.service.dto.*;
import io.yody.yosurvey.survey.service.helpers.ExcelHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
public class EmployeeSurveySubmitDataStrategy extends ExcelDataStrategy<EmployeeSurveySubmitCriteria, SurveySubmitDTO> {
    private final SurveySubmitQueryService surveySubmitQueryService;

    public EmployeeSurveySubmitDataStrategy(SurveySubmitQueryService surveySubmitQueryService) {
        this.surveySubmitQueryService = surveySubmitQueryService;
    }

    public static final int MAX_RECORD = 50000;
    public static final int PAGE_SIZE = 100;

    @Override
    public Page<SurveySubmitDTO> fetchPageData(EmployeeSurveySubmitCriteria criteria, Pageable pageable) {
        return surveySubmitQueryService.findAll(criteria, pageable);
    }

    @Override
    public List<ExcelDTO> convertPageData(List<SurveySubmitDTO> pageData) {
        return pageData.stream().map(dto -> new SurveySubmitExcelDTO()
                .code(dto.getCode())
                .name(dto.getName())
                .target(dto.getTarget())
                .targetName(dto.getTargetName())
                .fieldValue(dto.getFieldValue())
            ).collect(Collectors.toList());
    }
    @Override
    public HashMap<String, String> getHeaders() {
        return new HashMap<String, String>() {{
            put("A1", "STT");
            put("B1", "YD");
            put("C1", "Tên nhân sự");
            put("D1", "Đối tượng đánh giá");
            put("E1", "Tên đối tượng đánh giá");
            put("F1", "Trả lời");
        }};
    }

    @Override
    public HashMap<String, Integer> getColNum() {
        return new HashMap<String, Integer>() {{
            put("index", 0);
            put("code", 1);
            put("name", 2);
            put("target", 3);
            put("targetName", 4);
            put("fieldValue", 5);
        }};
    }

    @Override
    public int getMaxRecord() {
        return MAX_RECORD;
    }

    @Override
    public int getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public List<String> getIncludeCriteria() {
        return Arrays.asList("target", "survey_id", "field_id");
    }

    @Override
    public EmployeeSurveySubmitCriteria initializeCriteria(String conditions, ObjectMapper objectMapper) {
        EmployeeSurveySubmitCriteria criteria = new EmployeeSurveySubmitCriteria();
        if (StringUtils.isNoneBlank(conditions)) {
            criteria = ExcelHelper.convertParamToPojo(conditions,
                EmployeeSurveySubmitCriteria.class, objectMapper, getIncludeCriteria());

        }
        return criteria;
    }

    @Override
    public boolean isCriteriaValid(EmployeeSurveySubmitCriteria criteria) {
        // Implement validation logic for EmployeeSurveySubmitCriteria
        return criteria != null && !ObjectUtils.isEmpty(criteria.getSurveyId());
    }
}

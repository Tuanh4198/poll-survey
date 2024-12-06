package io.yody.yosurvey.survey.service.jobs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.service.EmployeeSurveyQueryService;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveyCriteria;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyExcelDTO;
import io.yody.yosurvey.survey.service.dto.ExcelDTO;
import io.yody.yosurvey.survey.service.helpers.ExcelHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeSurveyDataStrategy extends
    ExcelDataStrategy<EmployeeSurveyCriteria, EmployeeSurveyDTO> {
    private final EmployeeSurveyQueryService employeeSurveyQueryService;

    public EmployeeSurveyDataStrategy(EmployeeSurveyQueryService employeeSurveyQueryService) {
        this.employeeSurveyQueryService = employeeSurveyQueryService;
    }

    public static final int MAX_RECORD = 20000;
    public static final int PAGE_SIZE = 100;

    @Override
    public Page<EmployeeSurveyDTO> fetchPageData(EmployeeSurveyCriteria criteria, Pageable pageable) {
        return employeeSurveyQueryService.findByCriteriaAdmin(criteria, pageable);
    }

    private String convertStatus(SurveyStatusEnum statusEnum) {
        if (statusEnum.equals(SurveyStatusEnum.PENDING)) {
            return "Chờ bắt đầu";
        }
        if (statusEnum.equals(SurveyStatusEnum.NOT_ATTENDED)) {
            return "Chưa tham gia";
        }
        if (statusEnum.equals(SurveyStatusEnum.COMPLETED)) {
            return "Đã tham gia";
        }
        return "";
    }
    @Override
    public List<ExcelDTO> convertPageData(List<EmployeeSurveyDTO> pageData) {
        return pageData.stream().map(dto -> {
            String statusName = convertStatus(dto.getStatus());
            return new EmployeeSurveyExcelDTO()
                .code(dto.getCode())
                .name(dto.getName())
                .target(dto.getTarget())
                .targetName(dto.getTargetName())
                .status(statusName);
        }).collect(Collectors.toList());
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return new HashMap<String, String>() {{
            put("A1", "STT");
            put("B1", "YD");
            put("C1", "Đối tượng nhận đánh giá");
            put("D1", "Đối tượng được đánh giá");
            put("E1", "Trạng thái");
        }};
    }

    @Override
    public HashMap<String, Integer> getColNum() {
        return new HashMap<String, Integer>() {{
            put("index", 0);
            put("code", 1);
            put("name", 2);
            put("targetName", 3);
            put("status", 4);
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
        return Arrays.asList("status", "search", "survey_id");
    }

    @Override
    public EmployeeSurveyCriteria initializeCriteria(String conditions, ObjectMapper objectMapper) {
        EmployeeSurveyCriteria criteria = new EmployeeSurveyCriteria();
        if (StringUtils.isNoneBlank(conditions)) {
            criteria = ExcelHelper.convertParamToPojo(conditions,
                EmployeeSurveyCriteria.class, objectMapper, getIncludeCriteria());

        }
        return criteria;
    }

    @Override
    public boolean isCriteriaValid(EmployeeSurveyCriteria criteria) {
        // Implement validation logic for EmployeeSurveyCriteria
        return criteria != null && !ObjectUtils.isEmpty(criteria.getSurveyId());
    }
}

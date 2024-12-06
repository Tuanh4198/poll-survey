package io.yody.yosurvey.survey.service.jobs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yody.yosurvey.survey.service.dto.ExcelDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

// Generic Abstract Strategy Class
public abstract class ExcelDataStrategy<CriteriaType, DTO> {

    // Abstract methods to be implemented by subclasses
    public abstract HashMap<String, String> getHeaders();
    public abstract HashMap<String, Integer> getColNum();
    public abstract int getMaxRecord();
    public abstract int getPageSize();
    public abstract List<String> getIncludeCriteria();
    public abstract CriteriaType initializeCriteria(String conditions, ObjectMapper objectMapper);
    public abstract boolean isCriteriaValid(CriteriaType criteria);
    public abstract Page<DTO> fetchPageData(CriteriaType criteria, Pageable pageable);
    public abstract List<ExcelDTO> convertPageData(List<DTO> pageData);
}


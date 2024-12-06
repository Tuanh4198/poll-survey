package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.repository.EmployeeSurveyRepository;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.service.cache.EmployeeCache;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveyCriteria;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.helpers.MetafieldHelper;
import io.yody.yosurvey.survey.service.helpers.SurveyHelper;
import io.yody.yosurvey.survey.service.helpers.ThumbHelper;
import io.yody.yosurvey.survey.service.mapper.EmployeeSurveyMapper;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.errors.NtsValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EmployeeSurveyQueryService extends QueryService<EmployeeSurveyEntity> {
    private final Logger log = LoggerFactory.getLogger(EmployeeSurveyService.class);

    private final EmployeeSurveyRepository employeeSurveyRepository;

    private final EmployeeSurveyMapper employeeSurveyMapper;
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final MetafieldHelper metafieldHelper;
    private final ThumbHelper thumbHelper;
    private final EmployeeCache employeeCache;
    public EmployeeSurveyQueryService(EmployeeSurveyRepository employeeSurveyRepository, EmployeeSurveyMapper employeeSurveyMapper,
                                      SurveyRepository surveyRepository, SurveyMapper surveyMapper, MetafieldHelper metafieldHelper,
                                      ThumbHelper thumbHelper, EmployeeCache employeeCache) {
        this.employeeSurveyRepository = employeeSurveyRepository;
        this.employeeSurveyMapper = employeeSurveyMapper;
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.metafieldHelper = metafieldHelper;
        this.thumbHelper = thumbHelper;
        this.employeeCache = employeeCache;
    }

    private void enrichSurveyInfo(List<EmployeeSurveyDTO> employeeSurveyDTOS) {
        List<Long> surveyIds = employeeSurveyDTOS.stream().map(EmployeeSurveyDTO::getSurveyId).collect(Collectors.toList());
        List<SurveyEntity> surveys = surveyRepository.findAllByIdIn(surveyIds);
        List<SurveyDTO> surveyDTOS = surveyMapper.toDto(surveys);
        Map<Long, SurveyDTO> surveyDTOMap = surveyDTOS.stream().collect(Collectors.toMap(SurveyDTO::getId, Function.identity()));
        metafieldHelper.enrichMetafieldSurvey(surveyDTOS);
        thumbHelper.enrichThumb(surveyDTOS, SurveyDTO::getThumbUrl, SurveyDTO::setPresignThumbUrl);
        for (EmployeeSurveyDTO employeeSurveyDTO : employeeSurveyDTOS) {
            if (!Objects.isNull(employeeSurveyDTO.getSurveyId())) {
                SurveyDTO surveyDTO = surveyDTOMap.get(employeeSurveyDTO.getSurveyId());
                employeeSurveyDTO.setTitle(surveyDTO.getTitle());
                employeeSurveyDTO.setDescription(surveyDTO.getDescription());
                employeeSurveyDTO.setThumbUrl(surveyDTO.getThumbUrl());
                employeeSurveyDTO.setApplyTime(surveyDTO.getApplyTime());
                employeeSurveyDTO.setEndTime(surveyDTO.getEndTime());
                employeeSurveyDTO.setIsRequired(surveyDTO.getIsRequired());
                employeeSurveyDTO.setMetafields(surveyDTO.getMetafields());
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<EmployeeSurveyDTO> findByCriteria(EmployeeSurveyCriteria criteria, Pageable pageable) {
        log.debug("Request to get all criteria: {}, page: {}", criteria, pageable);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().get().toUpperCase();
        Instant applyTime = criteria.getApplyTime();
        Instant endTime = criteria.getEndTime();
        Long surveyId = criteria.getSurveyId();
        String search = criteria.getSearch();
        Page<EmployeeSurveyEntity> examEmployeeEntities = employeeSurveyRepository
            .findByCriteria(
                userCode, search,  surveyId, criteria.getStatus(), applyTime, endTime, pageable
            );
        List<EmployeeSurveyEntity> entityList = examEmployeeEntities.getContent();
        List<EmployeeSurveyDTO> dtoList = employeeSurveyMapper.toDto(entityList);
        enrichSurveyInfo(dtoList);
        Page<EmployeeSurveyDTO> examEmployeeDTOS = new PageImpl<>(dtoList, pageable, examEmployeeEntities.getTotalElements());
        return examEmployeeDTOS;
    }

    @Transactional(readOnly = true)
    public Page<EmployeeSurveyDTO> findByCriteriaAdmin(EmployeeSurveyCriteria criteria, Pageable pageable) {
        log.debug("Request to get all criteria: {}, page: {}", criteria, pageable);
        Instant applyTime = criteria.getApplyTime();
        Instant endTime = criteria.getEndTime();
        Long surveyId = criteria.getSurveyId();
        String search = criteria.getSearch();
        Page<EmployeeSurveyEntity> examEmployeeEntities = employeeSurveyRepository
            .findByCriteria(
                null, search, surveyId, criteria.getStatus(), applyTime, endTime, pageable
            );
        List<EmployeeSurveyEntity> entityList = examEmployeeEntities.getContent();
        List<EmployeeSurveyDTO> dtoList = employeeSurveyMapper.toDto(entityList);
        Page<EmployeeSurveyDTO> examEmployeeDTOS = new PageImpl<>(dtoList, pageable, examEmployeeEntities.getTotalElements());
        return examEmployeeDTOS;
    }

    /**
     * Get one employeeSurvey by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public EmployeeSurveyDTO findOne(Long id) {
        log.debug("Request to get employee survey : {}", id);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().get().toUpperCase();
        Optional<EmployeeSurveyEntity> employeeSurveyOptional = employeeSurveyRepository.findById(id);

        if (!employeeSurveyOptional.isPresent()) {
            throw new NtsValidationException("message", "Chưa có bài khảo sát");
        }
        EmployeeSurveyEntity employeeSurvey = employeeSurveyOptional.get();
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyMapper.toDto(employeeSurvey);

        Optional<SurveyEntity> surveyEntityOptional = surveyRepository.findById(employeeSurvey.getSurveyId());
        if (surveyEntityOptional.isPresent()) {
            SurveyDTO surveyDTO = surveyMapper.toDto(surveyEntityOptional.get());
            metafieldHelper.enrichMetafieldSurvey(List.of(surveyDTO));
            metafieldHelper.enrichMetafieldAssignStrategy(SurveyHelper.getAssignStrategyDtos(surveyDTO));
            metafieldHelper.enrichMetafieldBlocks(SurveyHelper.getBlockDtos(surveyDTO));
            metafieldHelper.enrichMetafieldBlockFields(SurveyHelper.getBlockFieldDtos(surveyDTO));
            employeeSurveyDTO.setTitle(surveyDTO.getTitle());
            employeeSurveyDTO.setApplyTime(surveyDTO.getApplyTime());
            employeeSurveyDTO.setEndTime(surveyDTO.getEndTime());
            employeeSurveyDTO.setThumbUrl(surveyDTO.getThumbUrl());
            employeeSurveyDTO.setDescription(surveyDTO.getDescription());
            employeeSurveyDTO.setIsRequired(surveyDTO.getIsRequired());
            employeeSurveyDTO.setAssignStrategies(surveyDTO.getAssignStrategies());
            employeeSurveyDTO.setBlocks(surveyDTO.getBlocks());
            employeeSurveyDTO.setMetafields(surveyDTO.getMetafields());
            thumbHelper.enrichThumb(List.of(surveyDTO), SurveyDTO::getThumbUrl, SurveyDTO::setPresignThumbUrl);
        } else {
            throw new NtsValidationException("message", "Bài khảo sát gốc không tồn tại");
        }

        return employeeSurveyDTO;
    }
}

package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.repository.BlockFieldsRepository;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveySubmitCriteria;
import io.yody.yosurvey.survey.service.dto.SurveySubmitDTO;
import io.yody.yosurvey.survey.service.mapper.SurveySubmitMapper;
import io.yody.yosurvey.survey.web.rest.response.SubmitFieldCountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SurveySubmitQueryService {
    private final Logger log = LoggerFactory.getLogger(SurveySubmitQueryService.class);

    private final SurveySubmitRepository surveySubmitRepository;

    private final SurveySubmitMapper surveySubmitMapper;
    private final BlockFieldsRepository blockFieldsRepository;
    public SurveySubmitQueryService(SurveySubmitRepository surveySubmitRepository, SurveySubmitMapper surveySubmitMapper,
                                    BlockFieldsRepository blockFieldsRepository) {
        this.surveySubmitRepository = surveySubmitRepository;
        this.surveySubmitMapper = surveySubmitMapper;
        this.blockFieldsRepository = blockFieldsRepository;
    }

    public SubmitFieldCountDTO getFieldsCount(Long surveyId, String target, List<Long> fieldIds) {
        Map<Long, Long> fieldCounts = new HashMap<>();
        List<Object[]> results = surveySubmitRepository.countFieldBySurveyIdAndFieldIds(surveyId, target, fieldIds);
        for (Object[] result : results) {
            BigInteger fieldId = (BigInteger) result[0];
            BigInteger count = (BigInteger) result[1];
            fieldCounts.put(fieldId.longValue(), count.longValue());
        }
        return new SubmitFieldCountDTO(fieldCounts);
    }

    @Transactional(readOnly = true)
    public Page<SurveySubmitDTO> findAll(EmployeeSurveySubmitCriteria criteria, Pageable pageable) {
        Long surveyId = criteria.getSurveyId();
        Long fieldId = criteria.getFieldId();
        String target = criteria.getTarget();
        Page<SurveySubmitEntity> page = surveySubmitRepository
            .findAllWithFilters(surveyId, fieldId, target, pageable);
        log.debug("Request to get all SurveySubmits");
        return page.map(surveySubmitMapper::toDto);
    }

    /**
     * Get one surveySubmit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SurveySubmitDTO> findOne(Long id) {
        log.debug("Request to get SurveySubmit : {}", id);
        return surveySubmitRepository.findById(id).map(surveySubmitMapper::toDto);
    }
}

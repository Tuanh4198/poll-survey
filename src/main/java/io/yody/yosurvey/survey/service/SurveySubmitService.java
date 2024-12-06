package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.dto.SurveySubmitDTO;
import io.yody.yosurvey.survey.service.mapper.SurveySubmitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SurveySubmitEntity}.
 */
@Service
@Transactional
public class SurveySubmitService {

    private final Logger log = LoggerFactory.getLogger(SurveySubmitService.class);

    private final SurveySubmitRepository surveySubmitRepository;

    private final SurveySubmitMapper surveySubmitMapper;

    public SurveySubmitService(SurveySubmitRepository surveySubmitRepository, SurveySubmitMapper surveySubmitMapper) {
        this.surveySubmitRepository = surveySubmitRepository;
        this.surveySubmitMapper = surveySubmitMapper;
    }

    /**
     * Save a surveySubmit.
     *
     * @param surveySubmitDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveySubmitDTO save(SurveySubmitDTO surveySubmitDTO) {
        log.debug("Request to save SurveySubmit : {}", surveySubmitDTO);
        SurveySubmitEntity surveySubmitEntity = surveySubmitMapper.toEntity(surveySubmitDTO);
        surveySubmitEntity = surveySubmitRepository.save(surveySubmitEntity);
        return surveySubmitMapper.toDto(surveySubmitEntity);
    }

    /**
     * Update a surveySubmit.
     *
     * @param surveySubmitDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveySubmitDTO update(SurveySubmitDTO surveySubmitDTO) {
        log.debug("Request to update SurveySubmit : {}", surveySubmitDTO);
        SurveySubmitEntity surveySubmitEntity = surveySubmitMapper.toEntity(surveySubmitDTO);
        surveySubmitEntity = surveySubmitRepository.save(surveySubmitEntity);
        return surveySubmitMapper.toDto(surveySubmitEntity);
    }

    /**
     * Partially update a surveySubmit.
     *
     * @param surveySubmitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SurveySubmitDTO> partialUpdate(SurveySubmitDTO surveySubmitDTO) {
        log.debug("Request to partially update SurveySubmit : {}", surveySubmitDTO);

        return surveySubmitRepository
            .findById(surveySubmitDTO.getId())
            .map(existingSurveySubmit -> {
                surveySubmitMapper.partialUpdate(existingSurveySubmit, surveySubmitDTO);

                return existingSurveySubmit;
            })
            .map(surveySubmitRepository::save)
            .map(surveySubmitMapper::toDto);
    }

    /**
     * Delete the surveySubmit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SurveySubmit : {}", id);
        surveySubmitRepository.deleteById(id);
    }
}

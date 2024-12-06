package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.service.AwsS3MediaProvider;
import io.yody.yosurvey.service.MediaProvider;
import io.yody.yosurvey.service.MediaProviderFactory;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity_;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.service.criteria.SurveyCriteria;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.helpers.MetafieldHelper;
import io.yody.yosurvey.survey.service.helpers.SurveyHelper;
import io.yody.yosurvey.survey.service.helpers.ThumbHelper;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import org.nentangso.core.security.NtsSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.StringFilter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SurveyQueryService extends QueryService<SurveyEntity> {
    private final Logger log = LoggerFactory.getLogger(SurveyQueryService.class);
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final MetafieldHelper metafieldHelper;
    private final ThumbHelper thumbHelper;
    public SurveyQueryService(SurveyRepository surveyRepository, SurveyMapper surveyMapper,
                              MetafieldHelper metafieldHelper, ThumbHelper thumbHelper) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.metafieldHelper = metafieldHelper;
        this.thumbHelper = thumbHelper;
    }

    /**
     * Get all the surveys.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SurveyDTO> findByCriteria(SurveyCriteria criteria, Pageable page) {
        log.debug("Request to get all criteria: {}, page: {}", criteria, page);
        final Specification<SurveyEntity> specification = createSpecification(criteria);
        Page<SurveyEntity> surveys = surveyRepository.findAll(specification, page);
        Page<SurveyDTO> surveyDTOS = surveys.map(surveyMapper::toDto);
        thumbHelper.enrichThumb(surveyDTOS.getContent(), SurveyDTO::getThumbUrl, SurveyDTO::setPresignThumbUrl);
        return surveyDTOS;
    }

    private SurveyDTO getDto(SurveyEntity entity) {
        SurveyDTO surveyDTO = surveyMapper.toDto(entity);
        metafieldHelper.enrichMetafieldSurvey(List.of(surveyDTO));
        metafieldHelper.enrichMetafieldAssignStrategy(SurveyHelper.getAssignStrategyDtos(surveyDTO));
        metafieldHelper.enrichMetafieldBlocks(SurveyHelper.getBlockDtos(surveyDTO));
        metafieldHelper.enrichMetafieldBlockFields(SurveyHelper.getBlockFieldDtos(surveyDTO));
        thumbHelper.enrichThumb(List.of(surveyDTO), SurveyDTO::getThumbUrl, SurveyDTO::setPresignThumbUrl);
        return surveyDTO;
    }
    /**
     * Get one survey by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public SurveyDTO findOne(Long id) {
        log.debug("Request to get Survey : {}", id);
        Optional<SurveyEntity> surveyEntityOptional = surveyRepository.findById(id);
        if (surveyEntityOptional.isPresent()) {
            SurveyDTO surveyDTO = getDto(surveyEntityOptional.get());
            return surveyDTO;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public SurveyDTO findOneByHash(String hash) {
        SurveyEntity survey = surveyRepository.findByHash(hash);
        SurveyDTO dto = getDto(survey);
        dto.setId(null);
        return dto;
    }
    protected Specification<SurveyEntity> createSpecification(SurveyCriteria criteria) {
        Specification<SurveyEntity> specification = Specification.where(null);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().get();
        StringFilter userCodeFilter = new StringFilter();
        userCodeFilter.setEquals(userCode);

        if (criteria != null) {
            specification = specification.and(buildStringSpecification(userCodeFilter, SurveyEntity_.createdBy));
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SurveyEntity_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), SurveyEntity_.title));
            }
            if (criteria.getApplyTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getApplyTime(), SurveyEntity_.applyTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), SurveyEntity_.endTime));
            }
        }
        return specification;
    }
}

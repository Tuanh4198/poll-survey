package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.SurveyEntity_;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity_;
import io.yody.yosurvey.survey.repository.SurveyTemplateRepository;
import io.yody.yosurvey.survey.service.criteria.SurveyCriteria;
import io.yody.yosurvey.survey.service.criteria.SurveyTemplateCriteria;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;
import io.yody.yosurvey.survey.service.helpers.MetafieldHelper;
import io.yody.yosurvey.survey.service.helpers.SurveyHelper;
import io.yody.yosurvey.survey.service.helpers.ThumbHelper;
import io.yody.yosurvey.survey.service.mapper.SurveyTemplateMapper;
import org.nentangso.core.security.NtsSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.StringFilter;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SurveyTemplateQueryService extends QueryService<SurveyTemplateEntity> {
    private final Logger log = LoggerFactory.getLogger(SurveyQueryService.class);
    private final SurveyTemplateRepository surveyRepository;
    private final SurveyTemplateMapper surveyMapper;
    private final MetafieldHelper metafieldHelper;
    private final ThumbHelper thumbHelper;
    public SurveyTemplateQueryService(SurveyTemplateRepository surveyRepository, SurveyTemplateMapper surveyMapper,
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
    public Page<SurveyTemplateDTO> findByCriteria(SurveyTemplateCriteria criteria, Pageable page) {
        log.debug("Request to get all criteria: {}, page: {}", criteria, page);
        final Specification<SurveyTemplateEntity> specification = createSpecification(criteria);
        Page<SurveyTemplateEntity> surveys = surveyRepository.findAll(specification, page);
        Page<SurveyTemplateDTO> surveyTemplateDTOS = surveys.map(surveyMapper::toDto);
        thumbHelper.enrichThumb(surveyTemplateDTOS.getContent(), SurveyTemplateDTO::getThumbUrl, SurveyTemplateDTO::setPresignThumbUrl);
        return surveyTemplateDTOS;
    }

    /**
     * Get one survey by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public SurveyTemplateDTO findOne(Long id) {
        log.debug("Request to get Survey : {}", id);
        Optional<SurveyTemplateEntity> surveyTemplateEntityOptional = surveyRepository.findById(id);
        if (surveyTemplateEntityOptional.isPresent()) {
            SurveyTemplateDTO surveyTemplateDTO = surveyMapper.toDto(surveyTemplateEntityOptional.get());
            metafieldHelper.enrichMetafieldSurveyTemplate(List.of(surveyTemplateDTO));
            metafieldHelper.enrichMetafieldAssignStrategyTemplate(SurveyHelper.getAssignStrategyTemplateDtos(surveyTemplateDTO));
            metafieldHelper.enrichMetafieldBlocksTemplate(SurveyHelper.getBlockTemplateDtos(surveyTemplateDTO));
            metafieldHelper.enrichMetafieldBlockFieldsTemplate(SurveyHelper.getBlockFieldTemplateDtos(surveyTemplateDTO));
            thumbHelper.enrichThumb(List.of(surveyTemplateDTO), SurveyTemplateDTO::getThumbUrl, SurveyTemplateDTO::setPresignThumbUrl);
            return surveyTemplateDTO;
        } else {
            return null;
        }
    }

    protected Specification<SurveyTemplateEntity> createSpecification(SurveyTemplateCriteria criteria) {
        Specification<SurveyTemplateEntity> specification = Specification.where(null);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().get();
        StringFilter userCodeFilter = new StringFilter();
        userCodeFilter.setEquals(userCode);
        if (criteria != null) {
            specification = specification.and(buildStringSpecification(userCodeFilter, SurveyEntity_.createdBy));
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SurveyTemplateEntity_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), SurveyTemplateEntity_.title));
            }
//            if (criteria.getApplyTime() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getApplyTime(), SurveyTemplateEntity_.applyTime));
//            }
//            if (criteria.getEndTime() != null) {
//                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), SurveyTemplateEntity_.endTime));
//            }
        }
        return specification;
    }
}

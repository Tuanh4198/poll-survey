package io.yody.yosurvey.survey.service.helpers;

import io.yody.yosurvey.survey.domain.MetafieldEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import io.yody.yosurvey.survey.repository.MetafieldRepository;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.repository.SurveyTemplateRepository;
import io.yody.yosurvey.survey.service.business.MetafieldBO;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.business.SurveyMetafieldBO;
import io.yody.yosurvey.survey.service.business.SurveyTemplateAggregate;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;
import io.yody.yosurvey.survey.service.mapper.MetafieldMapper;
import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import io.yody.yosurvey.survey.service.mapper.SurveyTemplateMapper;
import org.nentangso.core.domain.NtsMetafieldEntity;
import org.nentangso.core.service.errors.NtsValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class SurveyTransactionManager {
    private static final Logger log = LoggerFactory.getLogger(SurveyTransactionManager.class);
    private final SurveyRepository surveyRepository;
    private final SurveyTemplateRepository surveyTemplateRepository;
    private final SurveyMapper surveyMapper;
    private final MetafieldHelper metafieldHelper;
    private final SurveyTemplateMapper surveyTemplateMapper;
    public SurveyTransactionManager(SurveyRepository surveyRepository, SurveyMapper surveyMapper,
                                    MetafieldHelper metafieldHelper, SurveyTemplateMapper surveyTemplateMapper,
                                    SurveyTemplateRepository surveyTemplateRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.metafieldHelper = metafieldHelper;
        this.surveyTemplateMapper = surveyTemplateMapper;
        this.surveyTemplateRepository = surveyTemplateRepository;
    }
    public List<MetafieldEntity> getMetafieldsFromAggregate(SurveyAggregate aggregate) {
        List<MetafieldEntity> allMetaFields = MetafieldHelper.getAllMetafields(aggregate);

        return allMetaFields;
    }

    public List<MetafieldEntity> getMetafieldsFromAggregateTemplate(SurveyTemplateAggregate aggregate) {
        List<MetafieldEntity> allMetaFields = MetafieldHelper.getAllMetafields(aggregate);

        return allMetaFields;
    }
    private void saveTemplateMetafields(List<SurveyTemplateAggregate> aggregates) {
        Set<MetafieldEntity> uniqueMetafields = new HashSet<>();

        for (SurveyTemplateAggregate aggregate : aggregates) {
            List<MetafieldEntity> metafields = getMetafieldsFromAggregateTemplate(aggregate);
            uniqueMetafields.addAll(metafields);
        }
        // Convert the Set back to a List if required by the repository method
        List<MetafieldEntity> allMetafields = new ArrayList<>(uniqueMetafields);
        metafieldHelper.saveAll(allMetafields);
    }
    private void deleteMetafields(List<MetafieldEntity> metafields) {
        metafieldHelper.deleteAll(metafields);
    }

    private void saveMetafields(List<SurveyAggregate> aggregates) {
        Set<MetafieldEntity> uniqueMetafields = new HashSet<>();

        for (SurveyAggregate aggregate : aggregates) {
            List<MetafieldEntity> metafields = getMetafieldsFromAggregate(aggregate);
            uniqueMetafields.addAll(metafields);
        }
        // Convert the Set back to a List if required by the repository method
        List<MetafieldEntity> allMetafields = new ArrayList<>(uniqueMetafields);
        metafieldHelper.saveAll(allMetafields);
    }
    @Transactional(readOnly = true)
    public SurveyAggregate findById(Long surveyId) {
        SurveyEntity entity = surveyRepository.findById(surveyId).filter(Predicate.not(SurveyEntity::getDeleted)).orElse(null);
        if (entity == null) {
            throw new NtsValidationException("message", String.format("Không tìm thấy Survey %s", surveyId));
        }
        SurveyAggregate aggregate = surveyMapper.toAggregate(entity);
        return aggregate;
    }

    @Transactional(readOnly = true)
    public SurveyTemplateAggregate findTemplateById(Long surveyId) {
        SurveyTemplateEntity entity = surveyTemplateRepository.findById(surveyId)
            .filter(Predicate.not(SurveyTemplateEntity::getDeleted)).orElse(null);
        if (entity == null) {
            throw new NtsValidationException("message", String.format("Không tìm thấy Survey %s", surveyId));
        }
        SurveyTemplateAggregate aggregate = surveyTemplateMapper.toAggregate(entity);
        return aggregate;
    }

    @Transactional
    public SurveyDTO save(SurveyAggregate surveyAggregate) {
        SurveyEntity surveyEntity = this.surveyMapper.aggregateToEntity(surveyAggregate);
        surveyEntity = surveyRepository.save(surveyEntity);
        saveMetafields(List.of(surveyAggregate));
        SurveyDTO surveyDTO = this.surveyMapper.toDto(surveyEntity);
        return surveyDTO;
    }

    @Transactional
    public SurveyTemplateDTO saveTemplate(SurveyTemplateAggregate surveyAggregate) {
        SurveyTemplateEntity entity = this.surveyTemplateMapper.aggregateToEntity(surveyAggregate);
        entity = surveyTemplateRepository.save(entity);
        saveTemplateMetafields(List.of(surveyAggregate));
        SurveyTemplateDTO surveyDTO = this.surveyTemplateMapper.toDto(entity);
        return surveyDTO;
    }

    @Transactional
    public SurveyDTO update(List<MetafieldEntity> oldMetafields, SurveyAggregate aggregate) {
        deleteMetafields(oldMetafields);
        SurveyEntity entity = this.surveyMapper.aggregateToEntity(aggregate);
        entity = surveyRepository.save(entity);
        saveMetafields(List.of(aggregate));
        SurveyDTO surveyDTO = this.surveyMapper.toDto(entity);
        return surveyDTO;
    }
    @Transactional
    public SurveyTemplateDTO updateTemplate(List<MetafieldEntity> oldMetafields, SurveyTemplateAggregate aggregate) {
        deleteMetafields(oldMetafields);
        SurveyTemplateEntity entity = this.surveyTemplateMapper.aggregateToEntity(aggregate);
        entity = surveyTemplateRepository.save(entity);
        saveTemplateMetafields(List.of(aggregate));
        SurveyTemplateDTO surveyDTO = this.surveyTemplateMapper.toDto(entity);
        return surveyDTO;
    }
}

package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.MetafieldEntity;
import io.yody.yosurvey.survey.domain.SurveyTemplateEntity;
import io.yody.yosurvey.survey.domain.enumeration.AssignStrategyMetafieldEnum;
import io.yody.yosurvey.survey.domain.enumeration.ParticipantEmployeeTypeEnum;
import io.yody.yosurvey.survey.domain.enumeration.TargetTypeEnum;
import io.yody.yosurvey.survey.repository.SurveyTemplateRepository;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.business.SurveyTemplateAggregate;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;
import io.yody.yosurvey.survey.service.helpers.*;
import io.yody.yosurvey.survey.service.mapper.SurveyTemplateMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.yody.yosurvey.survey.web.rest.request.AssignStrategyRequest;
import io.yody.yosurvey.survey.web.rest.request.MetafieldRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service Implementation for managing {@link SurveyTemplateEntity}.
 */
@Service
@Transactional
public class SurveyTemplateService {

    private final Logger log = LoggerFactory.getLogger(SurveyTemplateService.class);

    private final SurveyTemplateRepository surveyTemplateRepository;

    private final SurveyTemplateMapper surveyTemplateMapper;
    private final IdHelper idHelper;
    private final SurveyTransactionManager transactionManager;
    private final MetafieldHelper metafieldHelper;
    public SurveyTemplateService(SurveyTemplateRepository surveyTemplateRepository, SurveyTemplateMapper surveyTemplateMapper,
                                 IdHelper idHelper, SurveyTransactionManager transactionManager, MetafieldHelper metafieldHelper) {
        this.surveyTemplateRepository = surveyTemplateRepository;
        this.surveyTemplateMapper = surveyTemplateMapper;
        this.idHelper = idHelper;
        this.transactionManager = transactionManager;
        this.metafieldHelper = metafieldHelper;
    }

    private void processTempId(SurveyRequest request) {
        idHelper.processSurveyId(List.of(request));
    }

    private void processImport(List<AssignStrategyRequest> requests) throws IOException {
        for (AssignStrategyRequest request : requests) {
            List<MetafieldRequest> metafieldRequests = request.getMetafields();
            List<String> participantCodes = new ArrayList<>();
            if (!Objects.isNull(request.getFileParticipant())) {
                participantCodes = ImportFileHelper.parseGroupUsersFromFile(request.getFileParticipant().getBase64());
            }
            if (!CollectionUtils.isEmpty(participantCodes)) {
                for (MetafieldRequest metafieldRequest : metafieldRequests) {
                    if (!AssignStrategyMetafieldEnum.inValidKey(metafieldRequest.getKey())) {
                        if (metafieldRequest.getKey().equals(AssignStrategyMetafieldEnum.PARTICIPANTS.getKey())) {
                            if (metafieldRequest.getType().equals(ParticipantEmployeeTypeEnum.SPEC_USERS.getKey())) {
                                metafieldRequest.setValue(String.join("||", participantCodes));
                            }
                        }
                    }
                };
            }
            List<String> targetCodes = new ArrayList<>();
            if (!Objects.isNull(request.getFileTargets())) {
                targetCodes = ImportFileHelper.parseGroupUsersFromFile(request.getFileTargets().getBase64());
            }
            if (!CollectionUtils.isEmpty(targetCodes)) {
                for (MetafieldRequest metafieldRequest : metafieldRequests) {
                    if (!AssignStrategyMetafieldEnum.inValidKey(metafieldRequest.getKey())) {
                        if (metafieldRequest.getKey().equals(AssignStrategyMetafieldEnum.TARGETS.getKey())) {
                            if (metafieldRequest.getType().equals(TargetTypeEnum.SPEC_USERS.getKey())) {
                                metafieldRequest.setValue(String.join("||", targetCodes));
                            }
                        }
                    }
                };
            }
        }
    }
    /**
     * Save a surveyTemplate.
     *
     * @param surveyTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyTemplateDTO save(SurveyRequest request) throws IOException {
        log.debug("Request to save Survey : {}", request);
        processTempId(request);
        processImport(request.getAssignStrategies());
        SurveyTemplateAggregate surveyAggregate = new SurveyTemplateAggregate(request);
        surveyAggregate.validate();
        SurveyTemplateDTO surveyDTO = transactionManager.saveTemplate(surveyAggregate);
        return surveyDTO;
    }

    /**
     * Update a surveyTemplate.
     *
     * @param surveyTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    public SurveyTemplateDTO update(SurveyRequest request) throws IOException {
        log.debug("Request to update SurveyTemplate : {}", request);
        processTempId(request);
        processImport(request.getAssignStrategies());
        SurveyTemplateAggregate aggregate = transactionManager.findTemplateById(request.getId());
        if (!Objects.isNull(aggregate)) {
            metafieldHelper.enrichMetafieldsSurveyTemplateAggregate(List.of(aggregate));
            metafieldHelper.enrichMetafieldsAssignStrategyTemplateBO(SurveyHelper.getAssignStrategyTemplateBos(aggregate));
            metafieldHelper.enrichMetafieldsBlockTemplateBO(SurveyHelper.getBlockTemplateBos(aggregate));
            metafieldHelper.enrichMetafieldsBlockFieldsTemplateBO(SurveyHelper.getBlockFieldBos(aggregate));
        }
        List<MetafieldEntity> oldMetafields = MetafieldHelper.getAllMetafields(aggregate);
        aggregate.update(request);
        aggregate.validate();
        SurveyTemplateDTO surveyDTO = transactionManager.updateTemplate(oldMetafields, aggregate);
        return surveyDTO;
    }

    /**
     * Partially update a surveyTemplate.
     *
     * @param surveyTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SurveyTemplateDTO> partialUpdate(SurveyTemplateDTO surveyTemplateDTO) {
        log.debug("Request to partially update SurveyTemplate : {}", surveyTemplateDTO);

        return surveyTemplateRepository
            .findById(surveyTemplateDTO.getId())
            .map(existingSurveyTemplate -> {
                surveyTemplateMapper.partialUpdate(existingSurveyTemplate, surveyTemplateDTO);

                return existingSurveyTemplate;
            })
            .map(surveyTemplateRepository::save)
            .map(surveyTemplateMapper::toDto);
    }

    /**
     * Get all the surveyTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SurveyTemplateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SurveyTemplates");
        return surveyTemplateRepository.findAll(pageable).map(surveyTemplateMapper::toDto);
    }

    /**
     * Get one surveyTemplate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SurveyTemplateDTO> findOne(Long id) {
        log.debug("Request to get SurveyTemplate : {}", id);
        return surveyTemplateRepository.findById(id).map(surveyTemplateMapper::toDto);
    }

    /**
     * Delete the surveyTemplate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SurveyTemplate : {}", id);
        surveyTemplateRepository.deleteById(id);
    }
}

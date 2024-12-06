package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.client.PegasusClient;
import io.yody.yosurvey.survey.domain.EmployeeSurveyEntity;
import io.yody.yosurvey.survey.domain.SurveyEntity;
import io.yody.yosurvey.survey.domain.SurveySubmitEntity;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.domain.enumeration.TargetTypeEnum;
import io.yody.yosurvey.survey.repository.EmployeeSurveyRepository;
import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.business.SurveyAggregate;
import io.yody.yosurvey.survey.service.cache.EmployeeCache;
import io.yody.yosurvey.survey.service.dto.BlockDTO;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.service.helpers.MetafieldHelper;
import io.yody.yosurvey.survey.service.helpers.NotifyHelper;
import io.yody.yosurvey.survey.service.helpers.SurveyHelper;
import io.yody.yosurvey.survey.service.mapper.EmployeeSurveyMapper;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import io.yody.yosurvey.survey.service.mapper.SurveyMapper;
import io.yody.yosurvey.survey.web.rest.request.EmployeeSurveyRequest;
import io.yody.yosurvey.survey.web.rest.request.FieldSubmitRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveySubmitRequest;
import org.nentangso.core.security.NtsSecurityUtils;
import org.nentangso.core.service.errors.NtsValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link EmployeeSurveyEntity}.
 */
@Service
@Transactional
public class EmployeeSurveyService {

    private final Logger log = LoggerFactory.getLogger(EmployeeSurveyService.class);

    private final EmployeeSurveyRepository employeeSurveyRepository;

    private final EmployeeSurveyMapper employeeSurveyMapper;
    private final SurveyRepository surveyRepository;
    private final SurveyMapper surveyMapper;
    private final MetafieldHelper metafieldHelper;
    private final SurveySubmitRepository surveySubmitRepository;
    private final EmployeeCache employeeCache;
    private final PegasusClient pegasusClient;
    public EmployeeSurveyService(EmployeeSurveyRepository employeeSurveyRepository, EmployeeSurveyMapper employeeSurveyMapper,
                                 SurveyRepository surveyRepository, SurveyMapper surveyMapper,
                                 MetafieldHelper metafieldHelper, SurveySubmitRepository surveySubmitRepository,
                                 EmployeeCache employeeCache, PegasusClient pegasusClient) {
        this.employeeSurveyRepository = employeeSurveyRepository;
        this.employeeSurveyMapper = employeeSurveyMapper;
        this.surveyRepository = surveyRepository;
        this.surveyMapper = surveyMapper;
        this.metafieldHelper = metafieldHelper;
        this.surveySubmitRepository = surveySubmitRepository;
        this.employeeCache = employeeCache;
        this.pegasusClient = pegasusClient;
    }

    private void validateRequest(EmployeeSurveyRequest request) {
        String targetType = request.getTargetType();
        if (TargetTypeEnum.inValidKey(targetType)) {
            throw new NtsValidationException("message", "Loại đối tượng đánh giá không hợp lệ");
        }
    }

    private void checkForDuplicate(Long surveyId, String target, String code) {
        List<EmployeeSurveyEntity> existingSurveys = employeeSurveyRepository.findBySurveyId(surveyId);

        Map<String, List<EmployeeSurveyEntity>> existingSurveysByTarget = existingSurveys.stream()
            .collect(Collectors.groupingBy(EmployeeSurveyEntity::getTarget));

        List<EmployeeSurveyEntity> surveysForTarget = existingSurveysByTarget.getOrDefault(target, new ArrayList<>());
        // Create a set of existing codes for easy lookup
        Set<String> existingCodes = surveysForTarget.stream()
            .map(EmployeeSurveyEntity::getCode)
            .collect(Collectors.toSet());
        if (existingCodes.contains(code)) {
            throw new NtsValidationException("message", "Bài đánh giá đã tồn tại");
        }
    }

    private SurveyStatusEnum getStatus(SurveyEntity survey) {
        SurveyStatusEnum status = SurveyStatusEnum.PENDING;
        Instant now = Instant.now();
        if (survey.getApplyTime().isBefore(now)) {
            status = SurveyStatusEnum.NOT_ATTENDED;
        };
        return status;
    }

    /**
     * Save a employeeSurvey.
     *
     * @param employeeSurveyDTO the entity to save.
     * @return the persisted entity.
     */
    public EmployeeSurveyDTO save(EmployeeSurveyRequest request) {
        log.debug("Request to save EmployeeSurvey : {}", request);
        validateRequest(request);
        String targetType = request.getTargetType();
        String participantType = request.getParticipantType();
        Long surveyId = request.getSurveyId();
        String target = request.getTarget();
        String code = request.getCode();

        checkForDuplicate(surveyId, target, code);

        Optional<SurveyEntity> surveyOptional = surveyRepository.findById(surveyId);
        if (!surveyOptional.isPresent()) {
            throw new NtsValidationException("message", "Bài khảo sát không tồn tại");
        }
        SurveyEntity survey = surveyOptional.get();
        SurveyStatusEnum status = getStatus(survey);
        String name = employeeCache.getNameByCode(code);
        String targetName = target;
        if (targetType.equals(TargetTypeEnum.SPEC_USERS.getKey())) {
            targetName = employeeCache.getNameByCode(target);
        }
        EmployeeSurveyEntity newSurvey = new EmployeeSurveyEntity()
            .surveyId(surveyId)
            .code(code)
            .name(name)
            .participantType(participantType)
            .target(target)
            .targetType(targetType)
            .targetName(targetName)
            .status(status);

        newSurvey = employeeSurveyRepository.save(newSurvey);
        NotifyHelper.sendNotifyBySurvey(survey.getThumbUrl(), List.of(newSurvey),
            pegasusClient::sendAssignSurvey, SurveyStatusEnum.NOT_ATTENDED);
        return employeeSurveyMapper.toDto(newSurvey);
    }

    /**
     * Update a employeeSurvey.
     *
     * @param employeeSurveyDTO the entity to save.
     * @return the persisted entity.
     */
    public EmployeeSurveyDTO update(EmployeeSurveyDTO employeeSurveyDTO) {
        log.debug("Request to update EmployeeSurvey : {}", employeeSurveyDTO);
        EmployeeSurveyEntity employeeSurveyEntity = employeeSurveyMapper.toEntity(employeeSurveyDTO);
        employeeSurveyEntity = employeeSurveyRepository.save(employeeSurveyEntity);
        return employeeSurveyMapper.toDto(employeeSurveyEntity);
    }

    public void submit(Long id, SurveySubmitRequest request) {
        log.debug("Request to submit {}", request);
        String userCode = NtsSecurityUtils.getCurrentUserLogin().get().toUpperCase();
        Optional<EmployeeSurveyEntity> employeeSurveyOptional = employeeSurveyRepository.findById(id);
        if (!employeeSurveyOptional.isPresent()) {
            throw new NtsValidationException("message", "Chưa có bài khảo sát");
        }
        EmployeeSurveyEntity employeeSurvey = employeeSurveyOptional.get();
        if (!employeeSurvey.getStatus().equals(SurveyStatusEnum.NOT_ATTENDED)) {
            throw new NtsValidationException("message", "Trạng thái bài khảo sát không hợp lệ");
        }

        Long surveyId = employeeSurvey.getSurveyId();
        Optional<SurveyEntity> surveyEntityOptional = surveyRepository.findById(surveyId);
        if (surveyEntityOptional.isPresent()) {
            SurveyEntity survey = surveyEntityOptional.get();
            SurveyDTO surveyDTO = surveyMapper.toDto(survey);
            metafieldHelper.enrichMetafieldBlocks(SurveyHelper.getBlockDtos(surveyDTO));
            metafieldHelper.enrichMetafieldBlockFields(SurveyHelper.getBlockFieldDtos(surveyDTO));

            List<FieldSubmitRequest> fieldSubmits = request.getFieldSubmits();
            List<BlockDTO> blocks = surveyDTO.getBlocks();
            Map<Long, Long> mapFieldIdToBlockId = new HashMap<>();
            Map<Long, BlockFieldsDTO> mapFieldIdToBlockFields = new HashMap<>();
            for (BlockDTO block : blocks) {
                List<BlockFieldsDTO> blockFields = block.getBlockFields();
                for (BlockFieldsDTO blockField : blockFields) {
                    mapFieldIdToBlockId.put(blockField.getId(), block.getId());
                    mapFieldIdToBlockFields.put(blockField.getId(), blockField);
                }
            }

            List<SurveySubmitEntity> surveySubmits = new ArrayList<>();
            for (FieldSubmitRequest fieldSubmit : fieldSubmits) {
                Long fieldId = fieldSubmit.getFieldId();
                Long blockId = fieldSubmit.getBlockId();
                BlockFieldsDTO blockField = mapFieldIdToBlockFields.get(fieldId);
                if (!mapFieldIdToBlockId.get(fieldId).equals(blockId)) {
                    throw new NtsValidationException("message",
                        String.format("Trường {} không thuộc block {}", fieldId, blockId));
                }
                String target = employeeSurvey.getTarget();
                String targetType = employeeSurvey.getTargetType();
                String targetName = target;
                if (targetType.equals(TargetTypeEnum.SPEC_USERS.getKey())) {
                    targetName = employeeCache.getNameByCode(target);
                }

                System.out.println("userCode " + userCode + " employeeCache.getNameByCode(userCode) " + employeeCache.getNameByCode(userCode));
                String fieldType = blockField.getType().getName();
                surveySubmits.add(
                    new SurveySubmitEntity()
                        .code(userCode)
                        .name(employeeCache.getNameByCode(userCode))
                        .target(target)
                        .targetName(targetName)
                        .surveyId(surveyId)
                        .blockId(blockId)
                        .type(fieldSubmit.getType())
                        .fieldId(fieldId)
                        .fieldName(blockField.getFieldName())
                        .fieldValue(fieldSubmit.getFieldValue())
                        .fieldType(fieldType)
                );
            }

            surveySubmitRepository.saveAll(surveySubmits);
            employeeSurvey.setStatus(SurveyStatusEnum.COMPLETED);
            employeeSurveyRepository.save(employeeSurvey);
        } else {
            throw new NtsValidationException("message", "Bài khảo sát không tồn tại");
        }
    }

    /**
     * Partially update a employeeSurvey.
     *
     * @param employeeSurveyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EmployeeSurveyDTO> partialUpdate(EmployeeSurveyDTO employeeSurveyDTO) {
        log.debug("Request to partially update EmployeeSurvey : {}", employeeSurveyDTO);

        return employeeSurveyRepository
            .findById(employeeSurveyDTO.getId())
            .map(existingEmployeeSurvey -> {
                employeeSurveyMapper.partialUpdate(existingEmployeeSurvey, employeeSurveyDTO);

                return existingEmployeeSurvey;
            })
            .map(employeeSurveyRepository::save)
            .map(employeeSurveyMapper::toDto);
    }

    /**
     * Delete the employeeSurvey by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete EmployeeSurvey : {}", id);
        employeeSurveyRepository.deleteById(id);
    }
}

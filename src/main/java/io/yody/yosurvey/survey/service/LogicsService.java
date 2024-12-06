package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.LogicsEntity;
import io.yody.yosurvey.survey.repository.LogicsRepository;
import io.yody.yosurvey.survey.service.dto.LogicsDTO;
import io.yody.yosurvey.survey.service.mapper.LogicsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link LogicsEntity}.
 */
@Service
@Transactional
public class LogicsService {

    private final Logger log = LoggerFactory.getLogger(LogicsService.class);

    private final LogicsRepository logicsRepository;

    private final LogicsMapper logicsMapper;

    public LogicsService(LogicsRepository logicsRepository, LogicsMapper logicsMapper) {
        this.logicsRepository = logicsRepository;
        this.logicsMapper = logicsMapper;
    }

    /**
     * Save a logics.
     *
     * @param logicsDTO the entity to save.
     * @return the persisted entity.
     */
    public LogicsDTO save(LogicsDTO logicsDTO) {
        log.debug("Request to save Logics : {}", logicsDTO);
        LogicsEntity logicsEntity = logicsMapper.toEntity(logicsDTO);
        logicsEntity = logicsRepository.save(logicsEntity);
        return logicsMapper.toDto(logicsEntity);
    }

    /**
     * Update a logics.
     *
     * @param logicsDTO the entity to save.
     * @return the persisted entity.
     */
    public LogicsDTO update(LogicsDTO logicsDTO) {
        log.debug("Request to update Logics : {}", logicsDTO);
        LogicsEntity logicsEntity = logicsMapper.toEntity(logicsDTO);
        logicsEntity = logicsRepository.save(logicsEntity);
        return logicsMapper.toDto(logicsEntity);
    }

    /**
     * Partially update a logics.
     *
     * @param logicsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LogicsDTO> partialUpdate(LogicsDTO logicsDTO) {
        log.debug("Request to partially update Logics : {}", logicsDTO);

        return logicsRepository
            .findById(logicsDTO.getId())
            .map(existingLogics -> {
                logicsMapper.partialUpdate(existingLogics, logicsDTO);

                return existingLogics;
            })
            .map(logicsRepository::save)
            .map(logicsMapper::toDto);
    }

    /**
     * Get all the logics.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LogicsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Logics");
        return logicsRepository.findAll(pageable).map(logicsMapper::toDto);
    }

    /**
     * Get one logics by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LogicsDTO> findOne(Long id) {
        log.debug("Request to get Logics : {}", id);
        return logicsRepository.findById(id).map(logicsMapper::toDto);
    }

    /**
     * Delete the logics by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Logics : {}", id);
        logicsRepository.deleteById(id);
    }
}

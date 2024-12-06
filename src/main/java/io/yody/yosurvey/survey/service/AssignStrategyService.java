package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.AssignStrategyEntity;
import io.yody.yosurvey.survey.repository.AssignStrategyRepository;
import io.yody.yosurvey.survey.service.dto.AssignStrategyDTO;
import io.yody.yosurvey.survey.service.mapper.AssignStrategyMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link AssignStrategyEntity}.
 */
@Service
@Transactional
public class AssignStrategyService {

    private final Logger log = LoggerFactory.getLogger(AssignStrategyService.class);

    private final AssignStrategyRepository assignStrategyRepository;

    private final AssignStrategyMapper assignStrategyMapper;

    public AssignStrategyService(AssignStrategyRepository assignStrategyRepository, AssignStrategyMapper assignStrategyMapper) {
        this.assignStrategyRepository = assignStrategyRepository;
        this.assignStrategyMapper = assignStrategyMapper;
    }

    /**
     * Save a assignStrategy.
     *
     * @param assignStrategyDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignStrategyDTO save(AssignStrategyDTO assignStrategyDTO) {
        log.debug("Request to save AssignStrategy : {}", assignStrategyDTO);
        AssignStrategyEntity assignStrategyEntity = assignStrategyMapper.toEntity(assignStrategyDTO);
        assignStrategyEntity = assignStrategyRepository.save(assignStrategyEntity);
        return assignStrategyMapper.toDto(assignStrategyEntity);
    }

    /**
     * Update a assignStrategy.
     *
     * @param assignStrategyDTO the entity to save.
     * @return the persisted entity.
     */
    public AssignStrategyDTO update(AssignStrategyDTO assignStrategyDTO) {
        log.debug("Request to update AssignStrategy : {}", assignStrategyDTO);
        AssignStrategyEntity assignStrategyEntity = assignStrategyMapper.toEntity(assignStrategyDTO);
        assignStrategyEntity = assignStrategyRepository.save(assignStrategyEntity);
        return assignStrategyMapper.toDto(assignStrategyEntity);
    }

    /**
     * Partially update a assignStrategy.
     *
     * @param assignStrategyDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssignStrategyDTO> partialUpdate(AssignStrategyDTO assignStrategyDTO) {
        log.debug("Request to partially update AssignStrategy : {}", assignStrategyDTO);

        return assignStrategyRepository
            .findById(assignStrategyDTO.getId())
            .map(existingAssignStrategy -> {
                assignStrategyMapper.partialUpdate(existingAssignStrategy, assignStrategyDTO);

                return existingAssignStrategy;
            })
            .map(assignStrategyRepository::save)
            .map(assignStrategyMapper::toDto);
    }

    /**
     * Get all the assignStrategies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AssignStrategyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all AssignStrategies");
        return assignStrategyRepository.findAll(pageable).map(assignStrategyMapper::toDto);
    }

    /**
     * Get one assignStrategy by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssignStrategyDTO> findOne(Long id) {
        log.debug("Request to get AssignStrategy : {}", id);
        return assignStrategyRepository.findById(id).map(assignStrategyMapper::toDto);
    }

    /**
     * Delete the assignStrategy by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete AssignStrategy : {}", id);
        assignStrategyRepository.deleteById(id);
    }
}

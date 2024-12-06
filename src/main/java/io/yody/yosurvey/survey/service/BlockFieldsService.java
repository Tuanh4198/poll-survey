package io.yody.yosurvey.survey.service;

import io.yody.yosurvey.survey.domain.BlockFieldsEntity;
import io.yody.yosurvey.survey.repository.BlockFieldsRepository;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import io.yody.yosurvey.survey.service.mapper.BlockFieldsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BlockFieldsEntity}.
 */
@Service
@Transactional
public class BlockFieldsService {

    private final Logger log = LoggerFactory.getLogger(BlockFieldsService.class);

    private final BlockFieldsRepository blockFieldsRepository;

    private final BlockFieldsMapper blockFieldsMapper;

    public BlockFieldsService(BlockFieldsRepository blockFieldsRepository, BlockFieldsMapper blockFieldsMapper) {
        this.blockFieldsRepository = blockFieldsRepository;
        this.blockFieldsMapper = blockFieldsMapper;
    }

    /**
     * Save a blockFields.
     *
     * @param blockFieldsDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockFieldsDTO save(BlockFieldsDTO blockFieldsDTO) {
        log.debug("Request to save BlockFields : {}", blockFieldsDTO);
        BlockFieldsEntity blockFieldsEntity = blockFieldsMapper.toEntity(blockFieldsDTO);
        blockFieldsEntity = blockFieldsRepository.save(blockFieldsEntity);
        return blockFieldsMapper.toDto(blockFieldsEntity);
    }

    /**
     * Update a blockFields.
     *
     * @param blockFieldsDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockFieldsDTO update(BlockFieldsDTO blockFieldsDTO) {
        log.debug("Request to update BlockFields : {}", blockFieldsDTO);
        BlockFieldsEntity blockFieldsEntity = blockFieldsMapper.toEntity(blockFieldsDTO);
        blockFieldsEntity = blockFieldsRepository.save(blockFieldsEntity);
        return blockFieldsMapper.toDto(blockFieldsEntity);
    }

    /**
     * Partially update a blockFields.
     *
     * @param blockFieldsDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BlockFieldsDTO> partialUpdate(BlockFieldsDTO blockFieldsDTO) {
        log.debug("Request to partially update BlockFields : {}", blockFieldsDTO);

        return blockFieldsRepository
            .findById(blockFieldsDTO.getId())
            .map(existingBlockFields -> {
                blockFieldsMapper.partialUpdate(existingBlockFields, blockFieldsDTO);

                return existingBlockFields;
            })
            .map(blockFieldsRepository::save)
            .map(blockFieldsMapper::toDto);
    }

    /**
     * Get all the blockFields.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<BlockFieldsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all BlockFields");
        return blockFieldsRepository.findAll(pageable).map(blockFieldsMapper::toDto);
    }

    /**
     * Get one blockFields by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlockFieldsDTO> findOne(Long id) {
        log.debug("Request to get BlockFields : {}", id);
        return blockFieldsRepository.findById(id).map(blockFieldsMapper::toDto);
    }

    /**
     * Delete the blockFields by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BlockFields : {}", id);
        blockFieldsRepository.deleteById(id);
    }
}

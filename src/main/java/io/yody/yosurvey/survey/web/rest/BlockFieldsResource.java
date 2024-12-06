package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.repository.BlockFieldsRepository;
import io.yody.yosurvey.survey.service.BlockFieldsService;
import io.yody.yosurvey.survey.service.dto.BlockFieldsDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.nentangso.core.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.BlockFieldsEntity}.
 */
@RestController
@RequestMapping("/api")
public class BlockFieldsResource {

    private final Logger log = LoggerFactory.getLogger(BlockFieldsResource.class);

    private static final String ENTITY_NAME = "blockFields";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BlockFieldsService blockFieldsService;

    private final BlockFieldsRepository blockFieldsRepository;

    public BlockFieldsResource(BlockFieldsService blockFieldsService, BlockFieldsRepository blockFieldsRepository) {
        this.blockFieldsService = blockFieldsService;
        this.blockFieldsRepository = blockFieldsRepository;
    }

    /**
     * {@code POST  /block-fields} : Create a new blockFields.
     *
     * @param blockFieldsDTO the blockFieldsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blockFieldsDTO, or with status {@code 400 (Bad Request)} if the blockFields has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/block-fields")
    public ResponseEntity<BlockFieldsDTO> createBlockFields(@Valid @RequestBody BlockFieldsDTO blockFieldsDTO) throws URISyntaxException {
        log.debug("REST request to save BlockFields : {}", blockFieldsDTO);
        if (blockFieldsDTO.getId() != null) {
            throw new BadRequestAlertException("A new blockFields cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BlockFieldsDTO result = blockFieldsService.save(blockFieldsDTO);
        return ResponseEntity
            .created(new URI("/api/block-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /block-fields/:id} : Updates an existing blockFields.
     *
     * @param id the id of the blockFieldsDTO to save.
     * @param blockFieldsDTO the blockFieldsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockFieldsDTO,
     * or with status {@code 400 (Bad Request)} if the blockFieldsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blockFieldsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/block-fields/{id}")
    public ResponseEntity<BlockFieldsDTO> updateBlockFields(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BlockFieldsDTO blockFieldsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BlockFields : {}, {}", id, blockFieldsDTO);
        if (blockFieldsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockFieldsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockFieldsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BlockFieldsDTO result = blockFieldsService.update(blockFieldsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, blockFieldsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /block-fields/:id} : Partial updates given fields of an existing blockFields, field will ignore if it is null
     *
     * @param id the id of the blockFieldsDTO to save.
     * @param blockFieldsDTO the blockFieldsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockFieldsDTO,
     * or with status {@code 400 (Bad Request)} if the blockFieldsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the blockFieldsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the blockFieldsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/block-fields/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BlockFieldsDTO> partialUpdateBlockFields(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BlockFieldsDTO blockFieldsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BlockFields partially : {}, {}", id, blockFieldsDTO);
        if (blockFieldsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockFieldsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockFieldsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BlockFieldsDTO> result = blockFieldsService.partialUpdate(blockFieldsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, blockFieldsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /block-fields} : get all the blockFields.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of blockFields in body.
     */
    @GetMapping("/block-fields")
    public ResponseEntity<List<BlockFieldsDTO>> getAllBlockFields(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of BlockFields");
        Page<BlockFieldsDTO> page = blockFieldsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /block-fields/:id} : get the "id" blockFields.
     *
     * @param id the id of the blockFieldsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blockFieldsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/block-fields/{id}")
    public ResponseEntity<BlockFieldsDTO> getBlockFields(@PathVariable Long id) {
        log.debug("REST request to get BlockFields : {}", id);
        Optional<BlockFieldsDTO> blockFieldsDTO = blockFieldsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blockFieldsDTO);
    }

    /**
     * {@code DELETE  /block-fields/:id} : delete the "id" blockFields.
     *
     * @param id the id of the blockFieldsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/block-fields/{id}")
    public ResponseEntity<Void> deleteBlockFields(@PathVariable Long id) {
        log.debug("REST request to delete BlockFields : {}", id);
        blockFieldsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.repository.AssignStrategyRepository;
import io.yody.yosurvey.survey.service.AssignStrategyService;
import io.yody.yosurvey.survey.service.dto.AssignStrategyDTO;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.AssignStrategyEntity}.
 */
@RestController
@RequestMapping("/api")
public class AssignStrategyResource {

    private final Logger log = LoggerFactory.getLogger(AssignStrategyResource.class);

    private static final String ENTITY_NAME = "assignStrategy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssignStrategyService assignStrategyService;

    private final AssignStrategyRepository assignStrategyRepository;

    public AssignStrategyResource(AssignStrategyService assignStrategyService, AssignStrategyRepository assignStrategyRepository) {
        this.assignStrategyService = assignStrategyService;
        this.assignStrategyRepository = assignStrategyRepository;
    }

    /**
     * {@code POST  /assign-strategies} : Create a new assignStrategy.
     *
     * @param assignStrategyDTO the assignStrategyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assignStrategyDTO, or with status {@code 400 (Bad Request)} if the assignStrategy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/assign-strategies")
    public ResponseEntity<AssignStrategyDTO> createAssignStrategy(@Valid @RequestBody AssignStrategyDTO assignStrategyDTO)
        throws URISyntaxException {
        log.debug("REST request to save AssignStrategy : {}", assignStrategyDTO);
        if (assignStrategyDTO.getId() != null) {
            throw new BadRequestAlertException("A new assignStrategy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AssignStrategyDTO result = assignStrategyService.save(assignStrategyDTO);
        return ResponseEntity
            .created(new URI("/api/assign-strategies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /assign-strategies/:id} : Updates an existing assignStrategy.
     *
     * @param id the id of the assignStrategyDTO to save.
     * @param assignStrategyDTO the assignStrategyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignStrategyDTO,
     * or with status {@code 400 (Bad Request)} if the assignStrategyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assignStrategyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/assign-strategies/{id}")
    public ResponseEntity<AssignStrategyDTO> updateAssignStrategy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssignStrategyDTO assignStrategyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AssignStrategy : {}, {}", id, assignStrategyDTO);
        if (assignStrategyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignStrategyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignStrategyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AssignStrategyDTO result = assignStrategyService.update(assignStrategyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignStrategyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /assign-strategies/:id} : Partial updates given fields of an existing assignStrategy, field will ignore if it is null
     *
     * @param id the id of the assignStrategyDTO to save.
     * @param assignStrategyDTO the assignStrategyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assignStrategyDTO,
     * or with status {@code 400 (Bad Request)} if the assignStrategyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assignStrategyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assignStrategyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/assign-strategies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssignStrategyDTO> partialUpdateAssignStrategy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssignStrategyDTO assignStrategyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AssignStrategy partially : {}, {}", id, assignStrategyDTO);
        if (assignStrategyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assignStrategyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assignStrategyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssignStrategyDTO> result = assignStrategyService.partialUpdate(assignStrategyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assignStrategyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /assign-strategies} : get all the assignStrategies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assignStrategies in body.
     */
    @GetMapping("/assign-strategies")
    public ResponseEntity<List<AssignStrategyDTO>> getAllAssignStrategies(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of AssignStrategies");
        Page<AssignStrategyDTO> page = assignStrategyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /assign-strategies/:id} : get the "id" assignStrategy.
     *
     * @param id the id of the assignStrategyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assignStrategyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/assign-strategies/{id}")
    public ResponseEntity<AssignStrategyDTO> getAssignStrategy(@PathVariable Long id) {
        log.debug("REST request to get AssignStrategy : {}", id);
        Optional<AssignStrategyDTO> assignStrategyDTO = assignStrategyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assignStrategyDTO);
    }

    /**
     * {@code DELETE  /assign-strategies/:id} : delete the "id" assignStrategy.
     *
     * @param id the id of the assignStrategyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/assign-strategies/{id}")
    public ResponseEntity<Void> deleteAssignStrategy(@PathVariable Long id) {
        log.debug("REST request to delete AssignStrategy : {}", id);
        assignStrategyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

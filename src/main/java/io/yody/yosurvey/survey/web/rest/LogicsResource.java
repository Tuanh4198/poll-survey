package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.repository.LogicsRepository;
import io.yody.yosurvey.survey.service.LogicsService;
import io.yody.yosurvey.survey.service.dto.LogicsDTO;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.LogicsEntity}.
 */
@RestController
@RequestMapping("/api")
public class LogicsResource {

    private final Logger log = LoggerFactory.getLogger(LogicsResource.class);

    private static final String ENTITY_NAME = "logics";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LogicsService logicsService;

    private final LogicsRepository logicsRepository;

    public LogicsResource(LogicsService logicsService, LogicsRepository logicsRepository) {
        this.logicsService = logicsService;
        this.logicsRepository = logicsRepository;
    }

    /**
     * {@code POST  /logics} : Create a new logics.
     *
     * @param logicsDTO the logicsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new logicsDTO, or with status {@code 400 (Bad Request)} if the logics has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/logics")
    public ResponseEntity<LogicsDTO> createLogics(@Valid @RequestBody LogicsDTO logicsDTO) throws URISyntaxException {
        log.debug("REST request to save Logics : {}", logicsDTO);
        if (logicsDTO.getId() != null) {
            throw new BadRequestAlertException("A new logics cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LogicsDTO result = logicsService.save(logicsDTO);
        return ResponseEntity
            .created(new URI("/api/logics/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /logics/:id} : Updates an existing logics.
     *
     * @param id the id of the logicsDTO to save.
     * @param logicsDTO the logicsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logicsDTO,
     * or with status {@code 400 (Bad Request)} if the logicsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the logicsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/logics/{id}")
    public ResponseEntity<LogicsDTO> updateLogics(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LogicsDTO logicsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Logics : {}, {}", id, logicsDTO);
        if (logicsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logicsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logicsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LogicsDTO result = logicsService.update(logicsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logicsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /logics/:id} : Partial updates given fields of an existing logics, field will ignore if it is null
     *
     * @param id the id of the logicsDTO to save.
     * @param logicsDTO the logicsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated logicsDTO,
     * or with status {@code 400 (Bad Request)} if the logicsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the logicsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the logicsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/logics/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LogicsDTO> partialUpdateLogics(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LogicsDTO logicsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Logics partially : {}, {}", id, logicsDTO);
        if (logicsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, logicsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!logicsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LogicsDTO> result = logicsService.partialUpdate(logicsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, logicsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /logics} : get all the logics.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of logics in body.
     */
    @GetMapping("/logics")
    public ResponseEntity<List<LogicsDTO>> getAllLogics(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Logics");
        Page<LogicsDTO> page = logicsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /logics/:id} : get the "id" logics.
     *
     * @param id the id of the logicsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the logicsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/logics/{id}")
    public ResponseEntity<LogicsDTO> getLogics(@PathVariable Long id) {
        log.debug("REST request to get Logics : {}", id);
        Optional<LogicsDTO> logicsDTO = logicsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(logicsDTO);
    }

    /**
     * {@code DELETE  /logics/:id} : delete the "id" logics.
     *
     * @param id the id of the logicsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/logics/{id}")
    public ResponseEntity<Void> deleteLogics(@PathVariable Long id) {
        log.debug("REST request to delete Logics : {}", id);
        logicsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

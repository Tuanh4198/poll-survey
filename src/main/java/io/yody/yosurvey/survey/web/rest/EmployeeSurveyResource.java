package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.domain.constant.ExportComponentConst;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import io.yody.yosurvey.survey.repository.EmployeeSurveyRepository;
import io.yody.yosurvey.survey.service.EmployeeSurveyQueryService;
import io.yody.yosurvey.survey.service.EmployeeSurveyService;
import io.yody.yosurvey.survey.service.ExportService;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveyCriteria;
import io.yody.yosurvey.survey.service.dto.EmployeeSurveyDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.yody.yosurvey.survey.service.dto.SurveyDTO;
import io.yody.yosurvey.survey.web.rest.request.EmployeeSurveyRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveyExportRequest;
import io.yody.yosurvey.survey.web.rest.request.SurveySubmitRequest;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.EmployeeSurveyEntity}.
 */
@RestController
@RequestMapping("/api")
public class EmployeeSurveyResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeSurveyResource.class);

    private static final String ENTITY_NAME = "employeeSurvey";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeSurveyService employeeSurveyService;
    private final EmployeeSurveyQueryService employeeSurveyQueryService;
    private final EmployeeSurveyRepository employeeSurveyRepository;
    private final ExportService exportService;

    public EmployeeSurveyResource(EmployeeSurveyService employeeSurveyService, EmployeeSurveyQueryService employeeSurveyQueryService,
                                  EmployeeSurveyRepository employeeSurveyRepository, ExportService exportService) {
        this.employeeSurveyService = employeeSurveyService;
        this.employeeSurveyQueryService = employeeSurveyQueryService;
        this.employeeSurveyRepository = employeeSurveyRepository;
        this.exportService = exportService;
    }

    /**
     * {@code POST  /employee-surveys} : Create a new employeeSurvey.
     *
     * @param employeeSurveyDTO the employeeSurveyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employeeSurveyDTO, or with status {@code 400 (Bad Request)} if the employeeSurvey has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/employee-surveys")
    public ResponseEntity<EmployeeSurveyDTO> createEmployeeSurvey(@Valid @RequestBody EmployeeSurveyRequest request)
        throws URISyntaxException {
        log.debug("REST request to save EmployeeSurvey : {}", request);
        if (request.getId() != null) {
            throw new BadRequestAlertException("A new employeeSurvey cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EmployeeSurveyDTO result = employeeSurveyService.save(request);
        return ResponseEntity
            .created(new URI("/api/employee-surveys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /employee-surveys/:id} : Updates an existing employeeSurvey.
     *
     * @param id the id of the employeeSurveyDTO to save.
     * @param employeeSurveyDTO the employeeSurveyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeeSurveyDTO,
     * or with status {@code 400 (Bad Request)} if the employeeSurveyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the employeeSurveyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/employee-surveys/{id}")
    public ResponseEntity<EmployeeSurveyDTO> updateEmployeeSurvey(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EmployeeSurveyDTO employeeSurveyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update EmployeeSurvey : {}, {}", id, employeeSurveyDTO);
        if (employeeSurveyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeSurveyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!employeeSurveyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EmployeeSurveyDTO result = employeeSurveyService.update(employeeSurveyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeSurveyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /employee-surveys/:id} : Partial updates given fields of an existing employeeSurvey, field will ignore if it is null
     *
     * @param id the id of the employeeSurveyDTO to save.
     * @param employeeSurveyDTO the employeeSurveyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeeSurveyDTO,
     * or with status {@code 400 (Bad Request)} if the employeeSurveyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the employeeSurveyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the employeeSurveyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/employee-surveys/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EmployeeSurveyDTO> partialUpdateEmployeeSurvey(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EmployeeSurveyDTO employeeSurveyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update EmployeeSurvey partially : {}, {}", id, employeeSurveyDTO);
        if (employeeSurveyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeSurveyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!employeeSurveyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EmployeeSurveyDTO> result = employeeSurveyService.partialUpdate(employeeSurveyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeSurveyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /employee-surveys} : get all the employeeSurveys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employeeSurveys in body.
     */
    @GetMapping("/employee-surveys")
    public ResponseEntity<List<EmployeeSurveyDTO>> getAllEmployeeSurveys(
        @RequestParam(value = "survey_id", required = false) Long surveyId,
        @RequestParam(value = "apply_time", required = false) Instant applyTime,
        @RequestParam(value = "end_time", required = false) Instant endTime,
        @RequestParam(value = "status", required = false) SurveyStatusEnum status,
        @RequestParam(value = "search", required = false) String search,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        EmployeeSurveyCriteria criteria = new EmployeeSurveyCriteria(surveyId, applyTime, endTime, status, search);
        log.debug("REST request to get a page of EmployeeSurveys");
        Page<EmployeeSurveyDTO> page = employeeSurveyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/admin-employee-surveys")
    public ResponseEntity<List<EmployeeSurveyDTO>> getAllEmployeeSurveysAdmin(
        @RequestParam(value = "survey_id", required = false) Long surveyId,
        @RequestParam(value = "apply_time", required = false) Instant applyTime,
        @RequestParam(value = "end_time", required = false) Instant endTime,
        @RequestParam(value = "status", required = false) SurveyStatusEnum status,
        @RequestParam(value = "search", required = false) String search,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of EmployeeSurveys");
        EmployeeSurveyCriteria criteria = new EmployeeSurveyCriteria(surveyId, applyTime, endTime, status, search);
        Page<EmployeeSurveyDTO> page = employeeSurveyQueryService.findByCriteriaAdmin(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/admin-employee-surveys/export")
    public ResponseEntity<Boolean> exportEmployeeSurvey(@Valid @RequestBody SurveyExportRequest request) {
        log.debug("REST request to export surveys");
        exportService.export(request, ExportComponentConst.EMPLOYEE_SURVEY);
        return ResponseEntity.ok().body(true);
    }
    /**
     * {@code GET  /employee-surveys/:id} : get the "id" employeeSurvey.
     *
     * @param id the id of the employeeSurveyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeSurveyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employee-surveys/{id}")
    public ResponseEntity<EmployeeSurveyDTO> getEmployeeSurvey(@PathVariable Long id) {
        log.debug("REST request to get EmployeeSurvey : {}", id);
        EmployeeSurveyDTO employeeSurveyDTO = employeeSurveyQueryService.findOne(id);
        return ResponseEntity.ok().body(employeeSurveyDTO);
    }

    @PostMapping("/employee-surveys/{id}/submit")
    public ResponseEntity<Boolean> submit(@PathVariable Long id
        , @Valid @RequestBody SurveySubmitRequest request) {
        log.debug("REST request to get EmployeeSurvey : {}", id);
        employeeSurveyService.submit(id, request);
        return ResponseEntity.ok().body(true);
    }

    /**
     * {@code DELETE  /employee-surveys/:id} : delete the "id" employeeSurvey.
     *
     * @param id the id of the employeeSurveyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/employee-surveys/{id}")
    public ResponseEntity<Void> deleteEmployeeSurvey(@PathVariable Long id) {
        log.debug("REST request to delete EmployeeSurvey : {}", id);
        employeeSurveyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

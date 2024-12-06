package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.repository.SurveyRepository;
import io.yody.yosurvey.survey.service.SurveyQueryService;
import io.yody.yosurvey.survey.service.SurveyService;
import io.yody.yosurvey.survey.service.criteria.SurveyCriteria;
import io.yody.yosurvey.survey.service.dto.SurveyDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.SurveyEntity}.
 */
@RestController
@RequestMapping("/api")
public class SurveyResource {

    private final Logger log = LoggerFactory.getLogger(SurveyResource.class);

    private static final String ENTITY_NAME = "survey";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SurveyService surveyService;
    private final SurveyQueryService surveyQueryService;
    private final SurveyRepository surveyRepository;

    public SurveyResource(SurveyService surveyService, SurveyQueryService surveyQueryService, SurveyRepository surveyRepository) {
        this.surveyService = surveyService;
        this.surveyRepository = surveyRepository;
        this.surveyQueryService = surveyQueryService;
    }

    /**
     * {@code POST  /surveys} : Create a new survey.
     *
     * @param surveyDTO the surveyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new surveyDTO, or with status {@code 400 (Bad Request)} if the survey has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/surveys")
    public ResponseEntity<SurveyDTO> createSurvey(@Valid @RequestBody SurveyRequest request) throws URISyntaxException, IOException {
        log.debug("REST request to save Survey : {}", request);
        if (request.getId() != null) {
            throw new BadRequestAlertException("A new survey cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SurveyDTO result = surveyService.save(request);
        return ResponseEntity
            .created(new URI("/api/surveys/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /surveys/:id} : Updates an existing survey.
     *
     * @param id the id of the surveyDTO to save.
     * @param surveyDTO the surveyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyDTO,
     * or with status {@code 400 (Bad Request)} if the surveyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the surveyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/surveys/{id}")
    public ResponseEntity<SurveyDTO> updateSurvey(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SurveyRequest request
    ) throws URISyntaxException, IOException {
        log.debug("REST request to update Survey : {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, request.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SurveyDTO result = surveyService.update(request);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, request.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /surveys/:id} : Partial updates given fields of an existing survey, field will ignore if it is null
     *
     * @param id the id of the surveyDTO to save.
     * @param surveyDTO the surveyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyDTO,
     * or with status {@code 400 (Bad Request)} if the surveyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the surveyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the surveyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/surveys/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SurveyDTO> partialUpdateSurvey(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SurveyDTO surveyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Survey partially : {}, {}", id, surveyDTO);
        if (surveyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SurveyDTO> result = surveyService.partialUpdate(surveyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /surveys} : get all the surveys.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of surveys in body.
     */
    @GetMapping("/surveys")
    public ResponseEntity<List<SurveyDTO>> getAllSurveys(SurveyCriteria criteria,  @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Surveys");
        Page<SurveyDTO> page = surveyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /surveys/:id} : get the "id" survey.
     *
     * @param id the id of the surveyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the surveyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/surveys/{id}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Long id) {
        log.debug("REST request to get Survey : {}", id);
        SurveyDTO surveyDTO = surveyQueryService.findOne(id);
        return ResponseEntity.ok().body(surveyDTO);
    }


    @GetMapping("/public/survey-by-hash/{hash}")
    public ResponseEntity<SurveyDTO> getByHash(@PathVariable String hash) {
        log.debug("REST request to get Survey : {}", hash);
        SurveyDTO surveyDTO = surveyQueryService.findOneByHash(hash);
        return ResponseEntity.ok().body(surveyDTO);
    }

    @PostMapping("/public/surveys/{hash}/submit")
    public ResponseEntity<Boolean> submit(@PathVariable String hash
        , @Valid @RequestBody SurveySubmitRequest request) {
        log.debug("REST request to submitSurvey : {}", hash);
        surveyService.submit(hash, request);
        return ResponseEntity.ok().body(true);
    }

    @PostMapping("/surveys/{id}/assign")
    public ResponseEntity<Boolean> assignSurvey(@PathVariable Long id) {
        log.debug("REST request to get Survey : {}", id);
        surveyService.assign(id);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/survey-hash/{id}")
    public ResponseEntity<String> getPublicUrl(@PathVariable Long id) {
        log.debug("REST request to get survey url : {}", id);
        String url = surveyService.getHash(id);
        return ResponseEntity.ok().body(url);
    }
    /**
     * {@code DELETE  /surveys/:id} : delete the "id" survey.
     *
     * @param id the id of the surveyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/surveys/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        log.debug("REST request to delete Survey : {}", id);
        surveyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

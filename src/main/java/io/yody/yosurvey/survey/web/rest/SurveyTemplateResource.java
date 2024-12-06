package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.repository.SurveyTemplateRepository;
import io.yody.yosurvey.survey.service.SurveyTemplateQueryService;
import io.yody.yosurvey.survey.service.SurveyTemplateService;
import io.yody.yosurvey.survey.service.criteria.SurveyTemplateCriteria;
import io.yody.yosurvey.survey.service.dto.SurveyTemplateDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.yody.yosurvey.survey.web.rest.request.SurveyRequest;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.SurveyTemplateEntity}.
 */
@RestController
@RequestMapping("/api")
public class SurveyTemplateResource {

    private final Logger log = LoggerFactory.getLogger(SurveyTemplateResource.class);

    private static final String ENTITY_NAME = "surveyTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SurveyTemplateService surveyTemplateService;
    private final SurveyTemplateQueryService surveyTemplateQueryService;
    private final SurveyTemplateRepository surveyTemplateRepository;

    public SurveyTemplateResource(SurveyTemplateService surveyTemplateService, SurveyTemplateQueryService surveyTemplateQueryService,
                                  SurveyTemplateRepository surveyTemplateRepository) {
        this.surveyTemplateService = surveyTemplateService;
        this.surveyTemplateQueryService = surveyTemplateQueryService;
        this.surveyTemplateRepository = surveyTemplateRepository;
    }

    /**
     * {@code POST  /survey-templates} : Create a new surveyTemplate.
     *
     * @param surveyTemplateDTO the surveyTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new surveyTemplateDTO, or with status {@code 400 (Bad Request)} if the surveyTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/survey-templates")
    public ResponseEntity<SurveyTemplateDTO> createSurveyTemplate(@RequestBody SurveyRequest request)
        throws URISyntaxException, IOException {
        log.debug("REST request to save SurveyTemplate : {}", request);
        if (request.getId() != null) {
            throw new BadRequestAlertException("A new surveyTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SurveyTemplateDTO result = surveyTemplateService.save(request);
        return ResponseEntity
            .created(new URI("/api/survey-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /survey-templates/:id} : Updates an existing surveyTemplate.
     *
     * @param id the id of the surveyTemplateDTO to save.
     * @param surveyTemplateDTO the surveyTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the surveyTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the surveyTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/survey-templates/{id}")
    public ResponseEntity<SurveyTemplateDTO> updateSurveyTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SurveyRequest request
    ) throws URISyntaxException, IOException {
        log.debug("REST request to update SurveyTemplate : {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, request.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SurveyTemplateDTO result = surveyTemplateService.update(request);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, request.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /survey-templates/:id} : Partial updates given fields of an existing surveyTemplate, field will ignore if it is null
     *
     * @param id the id of the surveyTemplateDTO to save.
     * @param surveyTemplateDTO the surveyTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated surveyTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the surveyTemplateDTO is not valid,
     * or with status {@code 404 (Not Found)} if the surveyTemplateDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the surveyTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/survey-templates/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SurveyTemplateDTO> partialUpdateSurveyTemplate(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SurveyTemplateDTO surveyTemplateDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update SurveyTemplate partially : {}, {}", id, surveyTemplateDTO);
        if (surveyTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, surveyTemplateDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!surveyTemplateRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SurveyTemplateDTO> result = surveyTemplateService.partialUpdate(surveyTemplateDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, surveyTemplateDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /survey-templates} : get all the surveyTemplates.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of surveyTemplates in body.
     */
    @GetMapping("/survey-templates")
    public ResponseEntity<List<SurveyTemplateDTO>> getAllSurveyTemplates(SurveyTemplateCriteria criteria,  @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SurveyTemplates");
        Page<SurveyTemplateDTO> page = surveyTemplateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /survey-templates/:id} : get the "id" surveyTemplate.
     *
     * @param id the id of the surveyTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the surveyTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/survey-templates/{id}")
    public ResponseEntity<SurveyTemplateDTO> getSurveyTemplate(@PathVariable Long id) {
        log.debug("REST request to get SurveyTemplate : {}", id);
        SurveyTemplateDTO surveyTemplateDTO = surveyTemplateQueryService.findOne(id);
        return ResponseEntity.ok().body(surveyTemplateDTO);
    }

    /**
     * {@code DELETE  /survey-templates/:id} : delete the "id" surveyTemplate.
     *
     * @param id the id of the surveyTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/survey-templates/{id}")
    public ResponseEntity<Void> deleteSurveyTemplate(@PathVariable Long id) {
        log.debug("REST request to delete SurveyTemplate : {}", id);
        surveyTemplateService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

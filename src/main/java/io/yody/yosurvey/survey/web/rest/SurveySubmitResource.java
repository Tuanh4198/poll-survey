package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.domain.constant.ExportComponentConst;
import io.yody.yosurvey.survey.repository.SurveySubmitRepository;
import io.yody.yosurvey.survey.service.ExportService;
import io.yody.yosurvey.survey.service.SurveySubmitQueryService;
import io.yody.yosurvey.survey.service.SurveySubmitService;
import io.yody.yosurvey.survey.service.criteria.EmployeeSurveySubmitCriteria;
import io.yody.yosurvey.survey.service.dto.SurveySubmitDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import io.yody.yosurvey.survey.web.rest.request.SurveyExportRequest;
import io.yody.yosurvey.survey.web.rest.response.SubmitFieldCountDTO;
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
 * REST controller for managing {@link io.yody.yosurvey.survey.domain.SurveySubmitEntity}.
 */
@RestController
@RequestMapping("/api")
public class SurveySubmitResource {

    private final Logger log = LoggerFactory.getLogger(SurveySubmitResource.class);

    private static final String ENTITY_NAME = "surveySubmit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SurveySubmitService surveySubmitService;
    private final SurveySubmitQueryService surveySubmitQueryService;
    private final ExportService exportService;
    public SurveySubmitResource(SurveySubmitService surveySubmitService, SurveySubmitRepository surveySubmitRepository,
                                SurveySubmitQueryService surveySubmitQueryService, ExportService exportService) {
        this.surveySubmitService = surveySubmitService;
        this.surveySubmitQueryService = surveySubmitQueryService;
        this.exportService = exportService;
    }
    /**
     * {@code GET  /survey-submits} : get all the surveySubmits.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of surveySubmits in body.
     */
    @GetMapping("/survey-submits")
    public ResponseEntity<List<SurveySubmitDTO>> getAllSurveySubmits(
        @RequestParam(name = "survey_id", required = true) Long surveyId,
        @RequestParam(name = "field_id", required = true) Long fieldId,
        @RequestParam(name = "target", required = true) String target,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of SurveySubmits");
        EmployeeSurveySubmitCriteria criteria = new EmployeeSurveySubmitCriteria();
        criteria.setSurveyId(surveyId);
        criteria.setTarget(target);
        criteria.setFieldId(fieldId);
        Page<SurveySubmitDTO> page = surveySubmitQueryService.findAll(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/survey-submits/export")
    public ResponseEntity<Boolean> exportSurveySubmits(@Valid @RequestBody SurveyExportRequest request) {
        log.debug("REST request to export surveys");
        exportService.export(request, ExportComponentConst.EMPLOYEE_SURVEY_SUBMIT);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/survey-submits/{surveyId}/fields-count")
    public ResponseEntity<SubmitFieldCountDTO> getFieldsCount(
        @PathVariable(value = "surveyId", required = true) Long surveyId,
        @RequestParam(name = "target", required = true) String target,
        @RequestParam(name = "field_ids", required = true) List<Long> fieldIds) {
        SubmitFieldCountDTO fieldCounts = surveySubmitQueryService.getFieldsCount(surveyId, target, fieldIds);
        return ResponseEntity.ok().body(fieldCounts);
    }

    /**
     * {@code GET  /survey-submits/:id} : get the "id" surveySubmit.
     *
     * @param id the id of the surveySubmitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the surveySubmitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/survey-submits/{id}")
    public ResponseEntity<SurveySubmitDTO> getSurveySubmit(@PathVariable Long id) {
        log.debug("REST request to get SurveySubmit : {}", id);
        Optional<SurveySubmitDTO> surveySubmitDTO = surveySubmitQueryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(surveySubmitDTO);
    }

    /**
     * {@code DELETE  /survey-submits/:id} : delete the "id" surveySubmit.
     *
     * @param id the id of the surveySubmitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/survey-submits/{id}")
    public ResponseEntity<Void> deleteSurveySubmit(@PathVariable Long id) {
        log.debug("REST request to delete SurveySubmit : {}", id);
        surveySubmitService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

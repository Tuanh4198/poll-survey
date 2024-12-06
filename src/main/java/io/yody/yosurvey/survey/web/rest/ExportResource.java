package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.service.ExportService;
import io.yody.yosurvey.survey.service.dto.ExportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportResource {
    private final Logger log = LoggerFactory.getLogger(ExportResource.class);

    private static final String ENTITY_NAME = "export";
    private final ExportService exportService;

    public ExportResource(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/exports/{checksum}")
    public ResponseEntity<ExportDTO> getExportProcess(
        @PathVariable(value = "checksum", required = true) final String checksum) {
        log.debug("REST request to get process {}", checksum);
        ExportDTO exportDTO = exportService.getExport(checksum);
        return ResponseEntity.ok().body(exportDTO);
    }
}

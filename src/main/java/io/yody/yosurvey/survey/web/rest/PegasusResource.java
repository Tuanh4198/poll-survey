package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.survey.client.PegasusClient;
import io.yody.yosurvey.survey.service.criteria.DepartmentCriteria;
import io.yody.yosurvey.survey.web.rest.response.DepartmentDTO;
import io.yody.yosurvey.survey.web.rest.response.EmployeeDTO;
import io.yody.yosurvey.survey.web.rest.response.PageableDto;
import io.yody.yosurvey.survey.web.rest.response.Result;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PegasusResource {

    private final PegasusClient pegasusClient;

    public PegasusResource(PegasusClient pegasusClient) {
        this.pegasusClient = pegasusClient;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getDepartments(
        @RequestParam(name = "code.doesNotContains", required = false) String codeNotContains,
        @RequestParam(name = "code.contains", required = false) String code,
        @RequestParam(name = "name.contains", required = false) String name,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "500") int limit
    ) {
        List<DepartmentDTO> departments = pegasusClient.getAllDepartments(codeNotContains, code, name, page, limit, null);
        return ResponseEntity.ok().body(departments);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getEmployees(
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "limit", defaultValue = "30") int limit,
        @RequestParam(name = "info", required = false) String name,
        @RequestParam(name = "departments", required = false) String departmentNames,
        @RequestParam(name = "positions", required = false) String positionNames,
        @RequestParam(name = "filter_represent", defaultValue = "false") Boolean filterRepresent,
        @RequestParam(name = "status", defaultValue = "WORKING") String status
    ) {
        Result<PageableDto<EmployeeDTO>> employees = pegasusClient.getAllEmployees(
            page,
            limit,
            name,
            departmentNames,
            positionNames,
            filterRepresent,
            status
        );
        return ResponseEntity.ok().body(employees.getData().getItems());
    }
}

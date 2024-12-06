package io.yody.yosurvey.survey.client;

import io.yody.yosurvey.survey.web.rest.request.NotifyBaseRequest;
import io.yody.yosurvey.survey.web.rest.request.NotifyListRequest;
import io.yody.yosurvey.survey.web.rest.response.DepartmentDTO;
import io.yody.yosurvey.survey.web.rest.response.EmployeeDTO;
import io.yody.yosurvey.survey.web.rest.response.PageableDto;
import io.yody.yosurvey.survey.web.rest.response.Result;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import org.nentangso.core.client.NtsAuthorizedFeignClient;
import org.springframework.web.bind.annotation.*;

@NtsAuthorizedFeignClient(name = "pegasus-client", url = "${pegasus.client.url}")
public interface PegasusClient {
    @GetMapping("/api/departments")
    List<DepartmentDTO> getAllDepartments(
        @RequestParam(name = "code.doesNotContains") String codeNotContains,
        @RequestParam(name = "code.contains") String code,
        @RequestParam(name = "name.contains") String name,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "30") int limit,
        @RequestParam(name = "sort", defaultValue = "id,asc") String sort
    );

    @GetMapping("/api/employees/search")
    Result<PageableDto<EmployeeDTO>> getAllEmployees(
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "limit", defaultValue = "30") int limit,
        @RequestParam(name = "info") String name,
        @RequestParam(name = "departments") String departmentNames,
        @RequestParam(name = "positions") String positionNames,
        @RequestParam(name = "filter_represent", defaultValue = "false") Boolean filterRepresent,
        @RequestParam(name = "status", defaultValue = "WORKING") String status
    );

    @GetMapping("/api/employees/search-by-employee-segment")
    String searchByEmployeeSegments(
        @RequestParam(value = "codes") @NotEmpty @Size(max = 20) List<String> codes,
        @RequestParam(value = "segments") @NotEmpty @Size(max = 5) List<String> segments
    );

    @GetMapping("/api/employees/search-by-department-segment")
    String searchByDepartmentSegments(
        @RequestParam(value = "departments") @NotEmpty String departments, // phân cách nhau bằng dấu ||
        @RequestParam(value = "segments") @NotEmpty @Size(max = 5) List<String> segments
    );

    @PostMapping("/api/external-notifications/assign-survey")
    void sendAssignSurvey(@RequestBody NotifyListRequest request);

    @PostMapping("/api/external-notifications/about-to-expire-survey")
    void sendAboutToExpireSurvey(@RequestBody NotifyListRequest request);
}

package io.yody.yosurvey.survey.service.cache;

import io.yody.yosurvey.survey.client.PegasusClient;
import io.yody.yosurvey.survey.web.rest.response.EmployeeDTO;
import io.yody.yosurvey.survey.web.rest.response.PageableDto;
import io.yody.yosurvey.survey.web.rest.response.Result;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class EmployeeCache {

    private final PegasusClient pegasusClient;
    private Map<String, String> employeeCodeNameMap = new HashMap<>();
    private List<EmployeeDTO> allEmployees = new ArrayList<>();

    public EmployeeCache(PegasusClient pegasusClient) {
        this.pegasusClient = pegasusClient;
    }

    private List<EmployeeDTO> getEmployeeByBatch(int page, int limit) {
        Result<PageableDto<EmployeeDTO>> employeeResult = pegasusClient.getAllEmployees(page, limit, null, null, null, false, "WORKING");
        PageableDto<EmployeeDTO> batch = employeeResult.getData();

        return batch.getItems();
    }
    private List<EmployeeDTO> getAllEmployees() {
        System.out.println("getAllEmployees");
        List<EmployeeDTO> employeeInfos = new ArrayList<>();
        int page = 1;
        int limit = 1000;
        int maxUser = 10000;
        List<EmployeeDTO> batch = getEmployeeByBatch(page, limit);

        while (page * limit < maxUser) {
            employeeInfos.addAll(batch);
            page++;
            batch = getEmployeeByBatch(page, limit);

            System.out.println("fetched " + page);
        }

        System.out.println(employeeInfos.size());

        return employeeInfos;
    }

    public void loadEmployeeCache() {
        try {
            // Fetch employee data
            List<EmployeeDTO> employeeData = getAllEmployees();

            allEmployees = employeeData;
            // Map the code and name
            employeeData.forEach(employee -> {
                employeeCodeNameMap.put(employee.getCode(), employee.getDisplayName());
            });
        } catch (Exception e) {
            System.out.println("load Employee Cache err " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 1,6,8 * * *")
    public void scheduledCacheRefresh() {
        loadEmployeeCache();
    }

    public String getNameByCode(String code) {
        if (!Objects.isNull(employeeCodeNameMap.get(code))) {
            return employeeCodeNameMap.get(code);
        } else return "";
    }

    public Map<String, String> getEmployeeCodeNameMap() {
        return employeeCodeNameMap;
    }
    public List<EmployeeDTO> getAllEmployeesCache() {
        return allEmployees;
    }
}

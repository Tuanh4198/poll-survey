package io.yody.yosurvey.survey.web.rest.response;

import java.io.Serializable;

public class EmployeeDTO implements Serializable {

    private String code;
    private String displayName;
    private String role;
    private String departmentName;

    public EmployeeDTO() {}

    public EmployeeDTO(String code, String name) {
        this.code = code;
        this.displayName = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}

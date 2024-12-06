package io.yody.yosurvey.survey.web.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DepartmentDTO {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("code")
    private String code;
    @JsonProperty("parent_id")
    private Long parentId;
    @JsonProperty("manager_code")
    private String managerCode;
    @JsonProperty("represent_code")
    private String representCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getManagerCode() {
        return managerCode;
    }

    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    public String getRepresentCode() {
        return representCode;
    }

    public void setRepresentCode(String representCode) {
        this.representCode = representCode;
    }
}

package io.yody.yosurvey.survey.service.dto;

import java.io.Serializable;

public class EmployeeSurveyExcelDTO implements ExcelDTO, Serializable {
    private int index;
    private String code;
    private String name;
    private String status;
    private String target;
    private String targetName;
    public String getCode() {
        return code;
    }
    public EmployeeSurveyExcelDTO code(String code) {
        setCode(code);
        return this;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public EmployeeSurveyExcelDTO name(String name) {
        setName(name);
        return this;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }
    public EmployeeSurveyExcelDTO status(String status) {
        setStatus(status);
        return this;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getTarget() {
        return target;
    }
    public EmployeeSurveyExcelDTO target(String target) {
        setTarget(target);
        return this;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetName() {
        return targetName;
    }
    public EmployeeSurveyExcelDTO targetName(String targetName) {
        setTargetName(targetName);
        return this;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

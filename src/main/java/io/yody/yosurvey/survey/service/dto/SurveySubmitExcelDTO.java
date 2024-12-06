package io.yody.yosurvey.survey.service.dto;

import java.io.Serializable;

public class SurveySubmitExcelDTO implements ExcelDTO, Serializable {
    private int index;
    private String code;
    private String name;
    private String target;
    private String targetName;
    private String blockTitle;
    private String fieldValue;
    public String getCode() {
        return code;
    }
    public SurveySubmitExcelDTO code(String code) {
        setCode(code);
        return this;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public SurveySubmitExcelDTO name(String name) {
        setName(name);
        return this;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBlockTitle() {
        return blockTitle;
    }
    public SurveySubmitExcelDTO blockTitle(String blockTitle) {
        setBlockTitle(blockTitle);
        return this;
    }
    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

    public String getFieldValue() {
        return fieldValue;
    }
    public SurveySubmitExcelDTO fieldValue(String fieldValue) {
        setFieldValue(fieldValue);
        return this;
    }
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getTarget() {
        return target;
    }
    public SurveySubmitExcelDTO target(String target) {
        setTarget(target);
        return this;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetName() {
        return targetName;
    }
    public SurveySubmitExcelDTO targetName(String targetName) {
        setTargetName(targetName);
        return this;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}

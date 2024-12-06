package io.yody.yosurvey.survey.web.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

interface BaseEnum<T> {
    T getValue();

    String getDisplayName();
}

enum CommonResponseCode implements BaseEnum<Integer> {
    SUCCESS(20000000, "Thành công");

    private final int value;
    private final String displayName;

    private CommonResponseCode(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS+00:00")
    private Date responseTime;

    private List<String> errors;
    private String requestId;

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(BaseEnum<Integer> enums, T data) {
        this.code = (Integer) enums.getValue();
        this.message = enums.getDisplayName();
        this.data = data;
        this.responseTime = new Timestamp(System.currentTimeMillis());
    }

    public Result(int code, String message, T data, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public Result(int code, String message, String requestId) {
        this.code = code;
        this.message = message;
        this.requestId = requestId;
    }

    public static <T> Result<T> ok(T data) {
        return new Result(CommonResponseCode.SUCCESS, data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result(CommonResponseCode.SUCCESS.getValue(), message, data);
    }

    public static <T> Result<T> error(String requestId, int errorCode, String message) {
        return new Result(errorCode, message, requestId);
    }

    public Result() {}

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Date getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

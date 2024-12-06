package io.yody.yosurvey.survey.domain.enumeration;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The SurveyStatusEnum enumeration.
 */
public enum SurveyStatusEnum {
    PENDING,
    NOT_ATTENDED,
    COMPLETED;
    public static HashMap<String, String> statusEnumMap() {
        return new HashMap<String, String>() {
            {
                put("NOT_ATTENDED", "Chưa tham gia");
                put("PENDING", "Chờ bắt đầu");
                put("COMPLETED", "Đã tham gia");
            }
        };
    }
}

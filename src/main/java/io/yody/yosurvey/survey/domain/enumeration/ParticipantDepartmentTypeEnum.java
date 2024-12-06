package io.yody.yosurvey.survey.domain.enumeration;

public enum ParticipantDepartmentTypeEnum {
    EMPLOYEE_SAME_DEPARTMENT("employee_same_department", "Nhân sự cùng phòng ban"),
    EMPLOYEE_OTHER_DEPARTMENT("employee_other_department", "Nhân sự khác phòng ban");

    private final String key;
    private final String description;

    ParticipantDepartmentTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (ParticipantDepartmentTypeEnum temp : values()) {
            if (temp.key.equalsIgnoreCase(key)) return false;
        }
        return true;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}

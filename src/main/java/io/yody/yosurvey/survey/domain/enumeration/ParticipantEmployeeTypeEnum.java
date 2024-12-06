package io.yody.yosurvey.survey.domain.enumeration;

public enum ParticipantEmployeeTypeEnum {
    EMPLOYEE_SAME_DEPARTMENT("employee_same_department", "Nhân sự cùng phòng ban"),
    DEPARTMENT_MANAGER("department_manager", "Quản lý trực tiếp theo phòng ban"),
    EMPLOYEE_SAME_LEVEL_SAME_DEPARTMENT("employee_same_level_same_department", "Nhân sự cùng cấp cùng phòng ban"),
    EMPLOYEE_LOWER_LEVEL_SAME_DEPARTMENT("employee_lower_level_same_department", "Nhân sự cấp dưới cùng phòng ban"),
    SELF("self", "Tự đánh giá"),
    SPEC_USERS("spec_users", "Danh sách nhân sự cụ thể"),
    ANY_USERS("any_users", "Bất kỳ nhân sự"),
    ANONYMOUS("ANONYMOUS", "Ẩn danh");

    private final String key;
    private final String description;

    ParticipantEmployeeTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (ParticipantEmployeeTypeEnum temp : values()) {
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

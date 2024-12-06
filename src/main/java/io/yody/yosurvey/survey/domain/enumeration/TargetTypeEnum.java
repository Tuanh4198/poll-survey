package io.yody.yosurvey.survey.domain.enumeration;

public enum TargetTypeEnum {
    DEPARTMENT("department", "Khảo sát phòng ban"),
    SPEC_USERS("spec_users", "Khảo sát nhân sự"),
    OTHER("other", "Khảo sát khác");

    private final String key;
    private final String description;

    TargetTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (TargetTypeEnum temp : values()) {
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

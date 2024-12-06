package io.yody.yosurvey.survey.domain.enumeration;

import org.springframework.util.ObjectUtils;

public enum AssignStrategyMetafieldEnum {
    PARTICIPANTS("participants", "Đối tượng tham gia"),
    TARGETS("targets", "Đối tượng được đánh giá");

    private final String key;
    private final String description;

    AssignStrategyMetafieldEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (AssignStrategyMetafieldEnum temp : values()) {
            if (temp.key.equalsIgnoreCase(key)) return false;
        }
        return true;
    }

    public static boolean validateValue(String key, String value) {
        if (key.equals(PARTICIPANTS.getKey())) {
            return !ObjectUtils.isEmpty(value) && value.length() < 20000;
        }
        if (key.equals(TARGETS.getKey())) {
            return !ObjectUtils.isEmpty(value) && value.length() < 20000;
        }
        return false;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}

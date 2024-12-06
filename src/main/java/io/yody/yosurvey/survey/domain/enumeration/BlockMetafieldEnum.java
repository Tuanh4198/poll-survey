package io.yody.yosurvey.survey.domain.enumeration;

import org.springframework.util.ObjectUtils;

public enum BlockMetafieldEnum {
    REQUIRED("required", "Bắt buộc trả lời");
    private final String key;
    private final String description;

    BlockMetafieldEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (BlockMetafieldEnum temp : values()) {
            if (temp.key.equalsIgnoreCase(key)) return false;
        }
        return true;
    }

    public static boolean validValue(String key, String value) {
        if (key.equals(REQUIRED.getKey())) {
            return value.equals("true") || value.equals("false");
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

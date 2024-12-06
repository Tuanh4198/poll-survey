package io.yody.yosurvey.survey.domain.enumeration;

public enum BlockFieldsMetafieldEnum {
    FORMAT("format", "Format dữ liệu"),
    DESCRIPTION("description", "Mô tả");
    private final String key;
    private final String description;

    BlockFieldsMetafieldEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (BlockFieldsMetafieldEnum temp : values()) {
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

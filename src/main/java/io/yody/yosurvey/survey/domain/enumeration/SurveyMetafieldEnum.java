package io.yody.yosurvey.survey.domain.enumeration;

public enum SurveyMetafieldEnum {
    EVENTS("events", "Trường hợp tham gia khảo sát"),
    HIDDEN_FIELD1("hidden_field1", "Giá trị truyền thêm"),
    HIDDEN_FIELD2("hidden_field2", "Giá trị truyền thêm"),
    HIDDEN_FIELD3("hidden_field3", "Giá trị truyền thêm");
    private final String key;
    private final String description;

    SurveyMetafieldEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (SurveyMetafieldEnum temp : values()) {
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

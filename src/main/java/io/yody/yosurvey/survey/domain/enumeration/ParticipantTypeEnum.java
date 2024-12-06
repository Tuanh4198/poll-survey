package io.yody.yosurvey.survey.domain.enumeration;

public enum ParticipantTypeEnum {
    OTHER("other", "Tạo thủ công");
    private final String key;
    private final String description;

    ParticipantTypeEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public static boolean inValidKey(String key) {
        for (ParticipantTypeEnum temp : values()) {
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

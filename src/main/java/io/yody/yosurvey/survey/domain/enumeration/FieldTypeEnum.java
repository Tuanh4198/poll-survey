package io.yody.yosurvey.survey.domain.enumeration;

/**
 * The FieldTypeEnum enumeration.
 */
public enum FieldTypeEnum {
    MULTIPLE_CHOICE_OPTION,
    DATE_INPUT,
    TEXT_INPUT,
    NUMBER_INPUT,
    STAR_OPTION,
    POINT_SCALE_OPTION,
    TEXT,
    IMAGE;

    public String getName() {
        return this.name();
    }
}

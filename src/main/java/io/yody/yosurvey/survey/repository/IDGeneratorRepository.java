package io.yody.yosurvey.survey.repository;

public interface IDGeneratorRepository {
    Long getLastId(String key);
}

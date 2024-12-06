package io.yody.yosurvey.survey.service.business.redis;

public interface IDGenerator {
    Long nextId(String key);
}

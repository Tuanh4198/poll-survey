package io.yody.yosurvey.survey.service.helpers;

@FunctionalInterface
public interface TriConsumer<BO, PARENT, REQUEST> {
    void accept(BO bo, PARENT parent, REQUEST request);
}

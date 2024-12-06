package io.yody.yosurvey.survey.web.rest.response;

import java.io.Serializable;
import java.util.List;

public class PageableDto<T> implements Serializable {

    private Metadata metadata;
    private List<T> items;

    public PageableDto(Metadata metadata, List<T> items) {
        this.metadata = metadata;
        this.items = items;
    }

    public PageableDto() {}

    public Metadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}

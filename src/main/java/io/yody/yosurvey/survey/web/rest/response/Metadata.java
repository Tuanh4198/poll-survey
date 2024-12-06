package io.yody.yosurvey.survey.web.rest.response;

import java.io.Serializable;

public class Metadata implements Serializable {

    private long total;
    private int page;
    private int limit;

    public int getPage() {
        return this.page + 1;
    }

    public Metadata(long total, int page, int limit) {
        this.total = total;
        this.page = page;
        this.limit = limit;
    }

    public Metadata() {}

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}

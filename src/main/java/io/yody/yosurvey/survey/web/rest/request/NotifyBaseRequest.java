package io.yody.yosurvey.survey.web.rest.request;

import java.io.Serializable;
import java.util.List;

public class NotifyBaseRequest implements Serializable {

    List<String> codes;
    private Long id;
    private String name;
    private String thumbUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}

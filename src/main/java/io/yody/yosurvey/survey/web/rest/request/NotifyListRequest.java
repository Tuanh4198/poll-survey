package io.yody.yosurvey.survey.web.rest.request;

import java.io.Serializable;
import java.util.Map;

public class NotifyListRequest implements Serializable {
    private Map<String, Long> codeIdMap;
    private String name;
    private String thumbUrl;

    public Map<String, Long> getCodeIdMap() {
        return codeIdMap;
    }

    public void setCodeIdMap(Map<String, Long> codeIdMap) {
        this.codeIdMap = codeIdMap;
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
}

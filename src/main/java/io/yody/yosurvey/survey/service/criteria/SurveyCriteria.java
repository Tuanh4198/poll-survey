package io.yody.yosurvey.survey.service.criteria;

import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;

public class SurveyCriteria implements Serializable, Criteria {
    private static final long serialVersionUID = 1L;
    private LongFilter id;
    private StringFilter title;
    private InstantFilter applyTime;
    private InstantFilter endTime;

    public SurveyCriteria() {
    }
    public SurveyCriteria(SurveyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.applyTime = other.applyTime == null ? null : other.applyTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
    }
    public SurveyCriteria(StringFilter title, InstantFilter applyTime, InstantFilter endTime) {
        this.title = title;
        this.applyTime = applyTime;
        this.endTime = endTime;
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public InstantFilter getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(InstantFilter applyTime) {
        this.applyTime = applyTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    @Override
    public Criteria copy() {
        return new SurveyCriteria(this);
    }
}

package io.yody.yosurvey.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yody.yosurvey.survey.domain.enumeration.SurveyStatusEnum;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.nentangso.core.domain.AbstractAuditingEntity;

/**
 * Bài khảo sát cho nhân viên
 */
@Entity
@Table(name = "employee_survey")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Where(clause = "deleted = false")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EmployeeSurveyEntity extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Id
     */
    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Mã nhân viên
     */
    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    /**
     * Tên nhân viên
     */
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
    @NotNull
    @Column(name = "participant_type", nullable = false)
    private String participantType;

    /**
     * Mã khảo sát gốc
     */
    @NotNull
    @Column(name = "survey_id", nullable = false)
    private Long surveyId;

    /**
     * Trạng thái
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SurveyStatusEnum status;

    @NotNull
    @Column(name = "target", nullable = false)
    private String target;

    @NotNull
    @Column(name = "target_type", nullable = false)
    private String targetType;

    @NotNull
    @Column(name = "target_name", nullable = false)
    private String targetName;

    @NotNull
    @Column(name = "deleted")
    private Boolean deleted = false;
    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EmployeeSurveyEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public EmployeeSurveyEntity code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public EmployeeSurveyEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSurveyId() {
        return this.surveyId;
    }

    public EmployeeSurveyEntity surveyId(Long surveyId) {
        this.setSurveyId(surveyId);
        return this;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public SurveyStatusEnum getStatus() {
        return this.status;
    }

    public EmployeeSurveyEntity status(SurveyStatusEnum status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(SurveyStatusEnum status) {
        this.status = status;
    }

    public EmployeeSurveyEntity survey(SurveyEntity survey) {
        this.survey(survey);
        return this;
    }

    public String getTarget() {
        return target;
    }

    public EmployeeSurveyEntity target(String target) {
        setTarget(target);
        return this;
    }
    public void setTarget(String target) {
        this.target = target;
    }

    public String getParticipantType() {
        return participantType;
    }
    public EmployeeSurveyEntity participantType(String participantType) {
        setParticipantType(participantType);
        return this;
    }
    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public String getTargetType() {
        return targetType;
    }
    public EmployeeSurveyEntity targetType(String targetType) {
        setTargetType(targetType);
        return this;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }
    public EmployeeSurveyEntity targetName(String targetName) {
        setTargetName(targetName);
        return this;
    }
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmployeeSurveyEntity)) {
            return false;
        }
        return id != null && id.equals(((EmployeeSurveyEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EmployeeSurveyEntity{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", surveyId=" + getSurveyId() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}

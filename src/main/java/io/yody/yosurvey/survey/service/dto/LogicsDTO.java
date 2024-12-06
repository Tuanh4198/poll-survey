package io.yody.yosurvey.survey.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link io.yody.yosurvey.survey.domain.LogicsEntity} entity.
 */
@Schema(description = "Logic")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LogicsDTO implements Serializable {

    /**
     * Id
     */
    @NotNull
    @Schema(description = "Id", required = true)
    private Long id;

    /**
     * Tên component
     */
    @NotNull
    @Schema(description = "Tên component", required = true)
    private String componentName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogicsDTO)) {
            return false;
        }

        LogicsDTO logicsDTO = (LogicsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, logicsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LogicsDTO{" +
            "id=" + getId() +
            ", componentName='" + getComponentName() + "'" +
            "}";
    }
}

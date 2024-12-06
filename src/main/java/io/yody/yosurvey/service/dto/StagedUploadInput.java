package io.yody.yosurvey.service.dto;

import io.yody.yosurvey.service.enumaration.StagedUploadHttpMethodType;
import io.yody.yosurvey.service.enumaration.StagedUploadResource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StagedUploadInput implements Serializable {

    /**
     * Size of the file to upload, in bytes. This is required for VIDEO and MODEL_3D resources.
     */
    private Long fileSize;

    /**
     * Media filename.
     */
    @NotBlank
    private String filename;

    /**
     * HTTP method to be used by the Staged Upload.
     */
    private StagedUploadHttpMethodType httpMethod;

    /**
     * Media MIME type.
     */
    @NotBlank
    private String mimeType;

    /**
     * Media resource.
     */
    @NotNull
    private StagedUploadResource resource;

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public StagedUploadHttpMethodType getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(StagedUploadHttpMethodType httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public StagedUploadResource getResource() {
        return resource;
    }

    public void setResource(StagedUploadResource resource) {
        this.resource = resource;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StagedUploadInput{" +
            "fileSize=" + fileSize +
            ", filename='" + filename + '\'' +
            ", httpMethod=" + httpMethod +
            ", mimeType='" + mimeType + '\'' +
            ", resource=" + resource +
            '}';
    }
}

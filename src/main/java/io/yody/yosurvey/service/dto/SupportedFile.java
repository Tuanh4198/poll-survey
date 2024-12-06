package io.yody.yosurvey.service.dto;

import java.io.Serializable;
import java.util.Collection;

public class SupportedFile implements Serializable {

    /**
     * Abbreviation
     */
    private final String abbreviation;

    /**
     * MIME type
     */
    private final String mimeType;

    /**
     * File extension(s)
     */
    private final Collection<String> fileExtensions;

    public SupportedFile(String abbreviation, String mimeType, Collection<String> fileExtensions) {
        this.abbreviation = abbreviation;
        this.mimeType = mimeType;
        this.fileExtensions = fileExtensions;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Collection<String> getFileExtensions() {
        return fileExtensions;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SupportedFile{" +
            "abbreviation='" + abbreviation + '\'' +
            ", mimeType='" + mimeType + '\'' +
            ", fileExtensions=" + fileExtensions +
            '}';
    }

    public static SupportedFile create(String abbreviation, String mimeType, Collection<String> fileExtensions) {
        return new SupportedFile(abbreviation, mimeType, fileExtensions);
    }
}

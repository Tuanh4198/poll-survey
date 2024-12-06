package io.yody.yosurvey.service;

import io.yody.yosurvey.service.dto.StagedUploadInput;
import io.yody.yosurvey.service.dto.StagedUploadTarget;
import io.yody.yosurvey.service.dto.SupportedFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface MediaProvider {
    Collection<SupportedFile> IMAGE_FILES = Arrays.asList(
        SupportedFile.create("APNG", "image/apng", Collections.singletonList("apng")),
        SupportedFile.create("AVIF", "image/avif", Collections.singletonList("avif")),
        SupportedFile.create("GIF", "image/gif", Collections.singletonList("gif")),
        SupportedFile.create("JPEG", "image/jpeg", Arrays.asList("jpg", "jpeg", "jfif", "pjpeg", "pjp")),
        SupportedFile.create("PNG", "image/png", Collections.singletonList("png")),
        SupportedFile.create("SVG", "image/svg+xml", Collections.singletonList("svg")),
        SupportedFile.create("WebP", "image/webp", Collections.singletonList("webp"))
    );

    Collection<SupportedFile> VIDEO_FILES = List.of(SupportedFile.create("MP4", "video/mp4", Collections.singletonList("mp4")));

    Collection<SupportedFile> DOCUMENT_FILES = Arrays.asList(
        SupportedFile.create("text", "text/plain", Collections.singletonList("text")),
        SupportedFile.create("pdf", "application/pdf", Collections.singletonList("pdf")),
        SupportedFile.create(
            "word",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            Collections.singletonList("ms_word")
        ),
        SupportedFile.create(
            "excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            Collections.singletonList("xlsx")
        ),
        SupportedFile.create(
            "powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            Collections.singletonList("powerpoint")
        )
    );

    int EXPIRES_IN_HOURS = 24;

    /**
     * Creates staged upload target URLs for each input and is the first step in the upload process. The returned upload targets with URLs can be used as endpoints to upload the files.
     *
     * @param stagedUploads Input for the mutation includes information needed to generate staged upload targets.
     * @return
     */
    List<StagedUploadTarget> createStagedUploads(List<StagedUploadInput> stagedUploads);
    String createPreSignedGetUrl(String url);
}

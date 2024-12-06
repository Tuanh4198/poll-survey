package io.yody.yosurvey.survey.web.rest;

import io.yody.yosurvey.service.AwsS3MediaProvider;
import io.yody.yosurvey.service.MediaProvider;
import io.yody.yosurvey.service.MediaProviderFactory;
import io.yody.yosurvey.service.dto.StagedUploadInput;
import io.yody.yosurvey.service.dto.StagedUploadTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StagedUploadResource {
    private final Logger log = LoggerFactory.getLogger(StagedUploadResource.class);

    private final MediaProviderFactory mediaProviderFactory;
    private final AwsS3MediaProvider awsS3MediaProvider;

    public StagedUploadResource(MediaProviderFactory mediaProviderFactory, AwsS3MediaProvider awsS3MediaProvider) {
        this.mediaProviderFactory = mediaProviderFactory;
        this.awsS3MediaProvider = awsS3MediaProvider;
    }

    /**
     * Creates staged upload target URLs for each input and is the first step in the upload process. The returned upload targets with URLs can be used as endpoints to upload the files.
     *
     * @param stagedUploads Input for the mutation includes information needed to generate staged upload targets.
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/staged-uploads")
    public ResponseEntity<List<StagedUploadTarget>> createStagedUploads(
        @RequestBody @Valid @Size(min = 1) List<StagedUploadInput> stagedUploads
    ) throws URISyntaxException {
        MediaProvider mediaProvider = mediaProviderFactory.getMediaProvider();
        List<StagedUploadTarget> results = mediaProvider.createStagedUploads(stagedUploads);

        return ResponseEntity.created(new URI("/api/staged-uploads")).body(results);
    }

    @GetMapping("/staged-uploads")
    public ResponseEntity<String> createStagedGetFile(@RequestParam(name = "url") String url) throws URISyntaxException {
        MediaProvider mediaProvider = mediaProviderFactory.getMediaProvider();
        String results = mediaProvider.createPreSignedGetUrl(url);

        return ResponseEntity.created(new URI("/api/staged-uploads")).body(results);
    }

    @PostMapping("/public-upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("resourceType") String resourceType) {
        log.debug("REST request to upload file : {}", file.getOriginalFilename());

        try {
            byte[] fileData = file.getBytes();
            String fileName = file.getOriginalFilename();
            String contentType = file.getContentType();

            // Add any additional metadata if required
            Map<String, String> metadata = new HashMap<>();
            metadata.put("resourceType", resourceType);

            String fileUrl = awsS3MediaProvider.upload(fileData, fileName, contentType, metadata);
            log.info("fileUrl {}", fileUrl);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            log.error("Error reading file data", e);
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}

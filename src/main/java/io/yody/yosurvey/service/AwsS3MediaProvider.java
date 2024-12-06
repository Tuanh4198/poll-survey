package io.yody.yosurvey.service;

import static io.yody.yosurvey.service.enumaration.StagedUploadResource.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import io.yody.yosurvey.config.AwsS3Properties;
import io.yody.yosurvey.service.dto.StagedUploadInput;
import io.yody.yosurvey.service.dto.StagedUploadParameter;
import io.yody.yosurvey.service.dto.StagedUploadTarget;
import io.yody.yosurvey.service.dto.SupportedFile;
import io.yody.yosurvey.service.enumaration.StagedUploadResource;

import java.io.File;
import java.net.URL;
import java.text.Normalizer;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.nentangso.core.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class AwsS3MediaProvider implements MediaProvider {

    private static final Logger log = LoggerFactory.getLogger(AwsS3MediaProvider.class);

    private static final String META_PREFIX = "x-amz-meta-";

    private final AwsS3Properties awsS3Properties;

    @SuppressWarnings("unused")
    private S3Client s3Client;

    @SuppressWarnings("unused")
    private S3Presigner s3Presigner;

    public AwsS3MediaProvider(AwsS3Properties awsS3Properties) {
        this.awsS3Properties = awsS3Properties;
    }

    @SuppressWarnings("unused")
    @PostConstruct
    public void initialize() {
        s3Client = S3Client.builder().region(awsS3Properties.getRegion()).credentialsProvider(DefaultCredentialsProvider.create()).build();
        s3Presigner =
            S3Presigner.builder().region(awsS3Properties.getRegion()).credentialsProvider(DefaultCredentialsProvider.create()).build();
    }

    public String upload(byte[] fileData, String fileName, String contentType, Map<String, String> metadata) {
        String bucketName = awsS3Properties.getBucketName();
        String key = generateObjectKey(fileName);

        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest
            .builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .metadata(metadata);

        PutObjectRequest putObjectRequest = putObjectRequestBuilder.build();

        try {
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
            log.info("File uploaded successfully. ETag: {}", response.eTag());
            Region region = awsS3Properties.getRegion();
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), key);

            return url;
        } catch (S3Exception e) {
            log.error("Error uploading file to S3", e);
            throw new BadRequestAlertException("Error uploading file to S3", "upload", "s3error");
        }
    }

    @Override
    public List<StagedUploadTarget> createStagedUploads(List<StagedUploadInput> stagedUploads) {
        if (CollectionUtils.isEmpty(stagedUploads)) {
            return Collections.emptyList();
        }
        List<StagedUploadTarget> stagedUploadTargets = new ArrayList<>();
        for (StagedUploadInput stagedUpload : stagedUploads) {
            validateStagedUploadResource(stagedUpload);
            StagedUploadTarget stagedUploadTarget = generatePreSignedUrl(
                stagedUpload.getFilename(),
                stagedUpload.getMimeType(),
                stagedUpload.getResource()
            );
            stagedUploadTargets.add(stagedUploadTarget);
        }
        return stagedUploadTargets;
    }

    private void validateStagedUploadResource(StagedUploadInput stagedUpload) {
        switch (stagedUpload.getResource()) {
            case IMAGE:
            case SHOP_IMAGE:
            case PRODUCT_IMAGE:
            case COLLECTION_IMAGE:
                validateImage(stagedUpload);
                break;
            case FILE:
                validateDocument(stagedUpload);
                break;
            case VIDEO:
                validateVideo(stagedUpload);
                break;
            //            case MODEL_3D:
            //            case TIMELINE:
            //            case URL_REDIRECT_IMPORT:
            //            case BULK_MUTATION_VARIABLES:
            //            default:
        }
    }

    private void validateImage(StagedUploadInput stagedUpload) {
        SupportedFile supportedFile = MediaProvider.IMAGE_FILES
            .stream()
            .filter(f -> StringUtils.equals(f.getMimeType(), stagedUpload.getMimeType()))
            .findFirst()
            .orElseThrow(() ->
                new BadRequestAlertException(
                    String.format("Mime type is not supported for %s resource.", stagedUpload.getResource()),
                    "stagedUpload",
                    "mimeType"
                )
            );
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(stagedUpload.getFilename()));
        if (!supportedFile.getFileExtensions().contains(extension)) {
            throw new BadRequestAlertException(
                String.format("File extension is not supported for %s resource.", stagedUpload.getResource()),
                "stagedUpload",
                "filename"
            );
        }
    }

    private void validateDocument(StagedUploadInput stagedUpload) {
        SupportedFile supportedFile = MediaProvider.DOCUMENT_FILES
            .stream()
            .filter(f -> StringUtils.equals(f.getMimeType(), stagedUpload.getMimeType()))
            .findFirst()
            .orElseThrow(() ->
                new BadRequestAlertException(
                    String.format("Mime type is not supported for %s resource.", stagedUpload.getResource()),
                    "stagedUpload",
                    "mimeType"
                )
            );
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(stagedUpload.getFilename()));
        if (!supportedFile.getFileExtensions().contains(extension)) {
            throw new BadRequestAlertException(
                String.format("File extension is not supported for %s resource.", stagedUpload.getResource()),
                "stagedUpload",
                "filename"
            );
        }
    }

    private void validateVideo(StagedUploadInput stagedUpload) {
        SupportedFile supportedFile = MediaProvider.VIDEO_FILES
            .stream()
            .filter(f -> StringUtils.equals(f.getMimeType(), stagedUpload.getMimeType()))
            .findFirst()
            .orElseThrow(() ->
                new BadRequestAlertException(
                    String.format("Mime type is not supported for %s resource.", stagedUpload.getResource()),
                    "stagedUpload",
                    "mimeType"
                )
            );
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(stagedUpload.getFilename()));
        if (!supportedFile.getFileExtensions().contains(extension)) {
            throw new BadRequestAlertException(
                String.format("File extension is not supported for %s resource.", stagedUpload.getResource()),
                "stagedUpload",
                "filename"
            );
        }
    }

    private StagedUploadTarget generatePreSignedUrl(String fileName, String mimeType, StagedUploadResource resource) {
        //        String bucketName = getBucketName(resource);
        String bucketName = awsS3Properties.getBucketName();
        String key = generateObjectKey(fileName);
        StagedUploadTarget stagedUploadTarget = new StagedUploadTarget();
        String asciiName = unaccent(fileName);
        String contentType = StringUtils.trimToNull(mimeType);
        if (!awsS3Properties.isMetadataDisabled()) {
            stagedUploadTarget.addParameter(String.format("%s%s", META_PREFIX, "resource"), String.valueOf(resource));
            stagedUploadTarget.addParameter(String.format("%s%s", META_PREFIX, "name"), asciiName);
            if (StringUtils.isNotBlank(contentType)) {
                stagedUploadTarget.addParameter("content-type", contentType);
            }
        }
        Map<String, String> metadata = stagedUploadTarget
            .getParameters()
            .stream()
            .filter(f -> !StringUtils.equals("content-type", f.getName()))
            .collect(Collectors.toMap(f -> StringUtils.replace(f.getName(), META_PREFIX, ""), StagedUploadParameter::getValue));
        URL preSignedURL = getPreSignedURL(bucketName, key, contentType, metadata);
        String resourceUrl = preSignedURL.toString();
        stagedUploadTarget.setResourceUrl(resourceUrl);
        String url = UriComponentsBuilder.fromUriString(resourceUrl).replaceQuery(null).build(Collections.emptyMap()).toString();
        stagedUploadTarget.setUrl(url);
        return stagedUploadTarget;
    }

    private String getBucketName(StagedUploadResource resource) {
        switch (resource) {
            case FILE:
                return awsS3Properties.getBucketFile();
            case IMAGE:
                return awsS3Properties.getBucketImage();
            case SHOP_IMAGE:
                return awsS3Properties.getBucketShopImage();
            case PRODUCT_IMAGE:
                return awsS3Properties.getBucketProductImage();
            case COLLECTION_IMAGE:
                return awsS3Properties.getBucketCollectionImage();
            case VIDEO:
                return awsS3Properties.getBucketVideo();
            case MODEL_3D:
                return awsS3Properties.getBucketModel3D();
            case TIMELINE:
                return awsS3Properties.getBucketTimeline();
            case BULK_MUTATION_VARIABLES:
                return awsS3Properties.getBucketBulkMutationVariables();
            case URL_REDIRECT_IMPORT:
                return awsS3Properties.getBucketUrlRedirectImport();
            default:
                throw new BadRequestAlertException("Resource is not supported yet.", "stagedUpload", "resource");
        }
    }

    public String generateObjectKey(String filename) {
        return generateObjectKey(filename, true);
    }

    @SuppressWarnings("SameParameterValue")
    public String generateObjectKey(String filename, boolean newName) {
        String extension = StringUtils.lowerCase(FilenameUtils.getExtension(filename));
        Instant now = Instant.now();
        return String.format(
            "%s-%s",
            DateFormatUtils.format(Date.from(now), "yyyy-MM-dd"),
            newName ? String.format("%s.%s", UUID.randomUUID(), extension) : unaccent(filename)
        );
    }

    private URL getPreSignedURL(String bucketName, String key, String contentType, Map<String, String> metadata) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .metadata(metadata)
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest
                .builder()
                .signatureDuration(Duration.ofHours(MediaProvider.EXPIRES_IN_HOURS))
                .putObjectRequest(objectRequest)
                .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return presignedRequest.url();
        } catch (S3Exception e) {
            log.error("Get pre-signed URL error", e);
            throw new BadRequestAlertException("Generate pre-signed URL error.", "stagedUpload", "base");
        }
    }

    public static String unaccent(String src) {
        return Normalizer.normalize(src, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String createPreSignedGetUrl(String url) {
        String key = extractFileName(url);

        GetObjectRequest objectRequest = GetObjectRequest.builder().bucket(awsS3Properties.getBucketName()).key(key).build();

        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest
            .builder()
            .signatureDuration(Duration.ofHours(1)) // The URL will expire in 1 hour.
            .getObjectRequest(objectRequest)
            .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(preSignRequest);
        log.info("Presigned URL: [{}]", presignedRequest.url().toString());
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();
    }

    public String extractFileName(String urlString) {
        try {
            // Regex pattern để trích xuất tên file
            Pattern pattern = Pattern.compile("/([^/?]+)(\\?.*)?$");

            // Sử dụng Matcher để so khớp pattern với chuỗi URL
            Matcher matcher = pattern.matcher(urlString);

            // Nếu tìm thấy, lấy group đầu tiên (tên file)
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return urlString;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package io.yody.yosurvey.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import java.io.Serializable;

@ConfigurationProperties(prefix = "aws.s3")
public class AwsS3Properties implements Serializable {

    private String bucketName;

    /**
     * S3 accessKeyId
     */
    private String accessKeyId;

    /**
     * S3 secretKey
     */
    private String secretKey;

    /**
     * AWS Region, default ap-southeast-1 (singapore)
     */
    private Region region = Region.AP_SOUTHEAST_1;

    /**
     * Metadata is disabled
     */
    private boolean metadataDisabled = false;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#FILE}
     */
    private String bucketFile;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#IMAGE}
     */
    private String bucketImage;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#SHOP_IMAGE}
     */
    private String bucketShopImage;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#PRODUCT_IMAGE}
     */
    private String bucketProductImage;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#COLLECTION_IMAGE}
     */
    private String bucketCollectionImage;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#VIDEO}
     */
    private String bucketVideo;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#MODEL_3D}
     */
    private String bucketModel3D;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#TIMELINE}
     */
    private String bucketTimeline;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#BULK_MUTATION_VARIABLES}
     */
    private String bucketBulkMutationVariables;

    /**
     * S3 bucket for {@link <%= packageName %>.domain.enumeration.StagedUploadResource#URL_REDIRECT_IMPORT}
     */
    private String bucketUrlRedirectImport;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public boolean isMetadataDisabled() {
        return metadataDisabled;
    }

    public void setMetadataDisabled(boolean metadataDisabled) {
        this.metadataDisabled = metadataDisabled;
    }

    public String getBucketFile() {
        return bucketFile;
    }

    public void setBucketFile(String bucketFile) {
        this.bucketFile = bucketFile;
    }

    public String getBucketImage() {
        return bucketImage;
    }

    public void setBucketImage(String bucketImage) {
        this.bucketImage = bucketImage;
    }

    public String getBucketShopImage() {
        return bucketShopImage;
    }

    public void setBucketShopImage(String bucketShopImage) {
        this.bucketShopImage = bucketShopImage;
    }

    public String getBucketProductImage() {
        return bucketProductImage;
    }

    public void setBucketProductImage(String bucketProductImage) {
        this.bucketProductImage = bucketProductImage;
    }

    public String getBucketCollectionImage() {
        return bucketCollectionImage;
    }

    public void setBucketCollectionImage(String bucketCollectionImage) {
        this.bucketCollectionImage = bucketCollectionImage;
    }

    public String getBucketVideo() {
        return bucketVideo;
    }

    public void setBucketVideo(String bucketVideo) {
        this.bucketVideo = bucketVideo;
    }

    public String getBucketModel3D() {
        return bucketModel3D;
    }

    public void setBucketModel3D(String bucketModel3D) {
        this.bucketModel3D = bucketModel3D;
    }

    public String getBucketTimeline() {
        return bucketTimeline;
    }

    public void setBucketTimeline(String bucketTimeline) {
        this.bucketTimeline = bucketTimeline;
    }

    public String getBucketBulkMutationVariables() {
        return bucketBulkMutationVariables;
    }

    public void setBucketBulkMutationVariables(String bucketBulkMutationVariables) {
        this.bucketBulkMutationVariables = bucketBulkMutationVariables;
    }

    public String getBucketUrlRedirectImport() {
        return bucketUrlRedirectImport;
    }

    public void setBucketUrlRedirectImport(String bucketUrlRedirectImport) {
        this.bucketUrlRedirectImport = bucketUrlRedirectImport;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AwsS3Properties{" +
            "region=" + region +
            ", metadataDisabled=" + metadataDisabled +
            ", bucketFile='" + bucketFile + '\'' +
            ", bucketImage='" + bucketImage + '\'' +
            ", bucketShopImage='" + bucketShopImage + '\'' +
            ", bucketProductImage='" + bucketProductImage + '\'' +
            ", bucketCollectionImage='" + bucketCollectionImage + '\'' +
            ", bucketVideo='" + bucketVideo + '\'' +
            ", bucketModel3D='" + bucketModel3D + '\'' +
            ", bucketTimeline='" + bucketTimeline + '\'' +
            ", bucketBulkMutationVariables='" + bucketBulkMutationVariables + '\'' +
            ", bucketUrlRedirectImport='" + bucketUrlRedirectImport + '\'' +
            '}';
    }
}

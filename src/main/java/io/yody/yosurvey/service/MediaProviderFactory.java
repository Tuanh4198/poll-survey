package io.yody.yosurvey.service;

import org.springframework.stereotype.Component;

@Component
public class MediaProviderFactory {

    private final AwsS3MediaProvider awsS3MediaProvider;

    public MediaProviderFactory(AwsS3MediaProvider awsS3MediaProvider) {
        this.awsS3MediaProvider = awsS3MediaProvider;
    }

    // Only supports Aws S3 for now.
    public MediaProvider getMediaProvider() {
        return awsS3MediaProvider;
    }
}


package com.dabom.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3UrlBuilder {

    public static final String S3_BASE_URL = "https://%s.s3.%s.amazonaws.com/%s";

    @Value("${websocket.allowed-origin}")
    private String frontServerAddress;
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${spring.cloud.aws.region.static}")
    private String region;

    public String buildPublicUrl(String s3Key) {
        return String.format("%s/%s", frontServerAddress, s3Key);
//        return String.format(S3_BASE_URL, bucketName, region, s3Key);
    }
}

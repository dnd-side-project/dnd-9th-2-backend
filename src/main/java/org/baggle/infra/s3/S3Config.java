package org.baggle.infra.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    // S3에 접근하는 AWS IAM 계정의 access key 입니다.
    @Value("${cloud.aws.credentials.access-key}")
    private String iamAccessKey;
    // S3에 접근하는 AWS IAM 계정의 secret key 입니다.
    @Value("${cloud.aws.credentials.secret-key}")
    private String iamSecretKey;
    // S3 서버가 위한 지역을 가져옵니다.
    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * S3에 접근 권한이 주워진 객체를 Spring Bean에 등록합니다.
     * param: none
     * return: S3에 접근 권한이 주워진 객체를 return 합니다.
     */
    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(iamAccessKey, iamSecretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region).enablePathStyleAccess()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}

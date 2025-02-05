package com.example.project_economic.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String accessSecret;

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean(name = "s3ClientRead")
  public AmazonS3 s3ClientRead() {
    AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
    AwsClientBuilder.EndpointConfiguration endpoint =
        new AwsClientBuilder.EndpointConfiguration("cloudfront.net", region);
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withEndpointConfiguration(endpoint)
        .build();
  }

  @Bean(name = "s3ClientWrite")
  public AmazonS3 s3ClientWrite() {
    AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(region)
        .build();
  }
}

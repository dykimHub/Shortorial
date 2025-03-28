package com.sleep.sleep.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

@Configuration
public class S3Config {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    /**
     * Presigned URL을 생성하는 S3Presigner Bean을 등록합니다.
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.AP_NORTHEAST_2) // AWS 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))   // 정적 자격 증명
                .build();
    }

    /**
     * 비동기 S3 클라이언트(S3AsyncClient) Bean을 등록합니다.
     */
    @Bean
    public S3AsyncClient s3AsyncClient() {
        // S3와 통신하는 네트워크 연결 자체를 설정
        // 비동기 HTTP 클라이언트 설정(Netty 기반; Netty는 비동기 이벤트 기반 네트워크 프레임워크)
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(50)  // 동시에 처리할 수 있는 요청 수
                .connectionTimeout(Duration.ofSeconds(60))  // 연결 타임아웃; 60초 동안 연결 안 되면 실패 처리
                .readTimeout(Duration.ofSeconds(60))  // 응답 읽기 타임아웃; 60초 초과 시 실패
                .writeTimeout(Duration.ofSeconds(60))  // 요청 쓰기 타임아웃; 60초 초과 시 실패
                .build();

        // 타임아웃 및 재시도 정책 커스터마이징
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))  // 전체 API 요청에 걸리는 최대 시간; 2분 초과 시 실패 처리
                .apiCallAttemptTimeout(Duration.ofSeconds(90))  // 개별 시도 당 최대 시간; 90초 초과 시 재시도 혹은 실패 처리
                .retryPolicy(RetryMode.STANDARD) // AWS 권장 재시도 정책 사용
                .build();

        return S3AsyncClient.builder()
                .region(Region.AP_NORTHEAST_2) // AWS 리전 설정
                .httpClient(httpClient) // 커스텀 HTTP 클라이언트
                .overrideConfiguration(overrideConfig)  // 커스텀 타임아웃 및 재시도 정책
                .build();
    }

}

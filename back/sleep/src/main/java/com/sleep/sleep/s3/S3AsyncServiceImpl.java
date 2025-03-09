package com.sleep.sleep.s3;



import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Publisher;
import software.amazon.awssdk.services.s3.waiters.S3AsyncWaiter;
import software.amazon.awssdk.utils.IoUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3AsyncServiceImpl {
    private final MemberService memberService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static S3AsyncClient s3AsyncClient;

    public static S3AsyncClient getAsyncClient() {
        if (s3AsyncClient == null) {
            /*
            The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version 2,
            and it is designed to provide a high-performance, asynchronous HTTP client for interacting with AWS services.
             It uses the Netty framework to handle the underlying network communication and the Java NIO API to
             provide a non-blocking, event-driven approach to HTTP requests and responses.
             */

            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                    .maxConcurrency(50)  // Adjust as needed.
                    .connectionTimeout(Duration.ofSeconds(60))  // Set the connection timeout.
                    .readTimeout(Duration.ofSeconds(60))  // Set the read timeout.
                    .writeTimeout(Duration.ofSeconds(60))  // Set the write timeout.
                    .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                    .apiCallTimeout(Duration.ofMinutes(2))  // Set the overall API call timeout.
                    .apiCallAttemptTimeout(Duration.ofSeconds(90))  // Set the individual call attempt timeout.
                    .retryPolicy(RetryMode.STANDARD)
                    .build();

            s3AsyncClient = S3AsyncClient.builder()
                    .region(Region.AP_NORTHEAST_2)
                    .httpClient(httpClient)
                    .overrideConfiguration(overrideConfig)
                    .build();
        }
        return s3AsyncClient;
    }

    public CompletableFuture<Void> findS3Object(String keyName, String path) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(keyName)
                .bucket(bucketName)
                .build();

        CompletableFuture<ResponseBytes<GetObjectResponse>> response = getAsyncClient().getObject(objectRequest, AsyncResponseTransformer.toBytes());
        return response.thenAccept(objectBytes -> {
            try {
                byte[] data = objectBytes.asByteArray();
                Path filePath = Paths.get(path);
                Files.write(filePath, data);
                log.info("Successfully obtained bytes from an S3 object");
            } catch (IOException ex) {
                throw new RuntimeException("Failed to write data to file", ex);
            }
        }).whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to get object bytes from S3", ex);
            }
        });
    }




    public String createPresignedGetUrl(String keyName) {
        try (S3Presigner presigner = S3Presigner.create()) {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            log.info("Presigned URL: [{}]", presignedRequest.url().toString());
            log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

            return presignedRequest.url().toExternalForm();
        }
    }

//    /* Create a presigned URL to use in a subsequent PUT request */
//    public String createPresignedUrl(String bucketName, String keyName, Map<String, String> metadata) {
//        try (S3Presigner presigner = S3Presigner.create()) {
//
//            PutObjectRequest objectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(keyName)
//                    .metadata(metadata)
//                    .build();
//
//            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
//                    .putObjectRequest(objectRequest)
//                    .build();
//
//
//            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
//            String myURL = presignedRequest.url().toString();
//            logger.info("Presigned URL to upload a file to: [{}]", myURL);
//            logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
//
//            return presignedRequest.url().toExternalForm();
//        }
//    }



}


package com.sleep.sleep.s3;



import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;


import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3AsyncServiceImpl {
    private final MemberService memberService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Presigner presigner;
    private final JwtTokenUtil jwtTokenUtil;
    private S3AsyncClient s3AsyncClient;

    public S3AsyncClient getAsyncClient() {
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

//    /**
//     * Asynchronously retrieves the bytes of an object from an Amazon S3 bucket and writes them to a local file.
//     *
//     * @param bucketName the name of the S3 bucket containing the object
//     * @param keyName    the key (or name) of the S3 object to retrieve
//     * @param path       the local file path where the object's bytes will be written
//     * @return a {@link CompletableFuture} that completes when the object bytes have been written to the local file
//     */
    public CompletableFuture<byte[]> getObjectBytesAsync(String s3Key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(s3Key)
                .bucket(bucketName)
                .build();

        CompletableFuture<ResponseBytes<GetObjectResponse>> response = getAsyncClient().getObject(objectRequest, AsyncResponseTransformer.toBytes());
        return response.thenApply(objectBytes -> {
            try {
                byte[] data = objectBytes.asByteArray();
                //Path filePath = Paths.get(path);
                //Files.write(filePath, data);
                log.info("Successfully obtained bytes from an S3 object");
                return data;
            } catch (Exception ex) {
                throw new RuntimeException("Failed to write data to file", ex);
            }
        }).whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to get object bytes from S3", ex);
            }
        });
    }




    public String createPresignedGetUrl(String keyName) {


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

    public String createPresignedPutURL(String accessToken, String createdAt) {
        String memberId = jwtTokenUtil.getUsername(accessToken.substring(7));
        String fileName = memberId + "/" + createdAt;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                //.contentType("video/mp4")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        log.info("Presigned URL: [{}]", presignedRequest.url().toString());
        log.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url().toExternalForm();
    }

//    public byte[] findBlobOfS3Object(String s3key) throws IOException {
//        InputStream inputStream = getObjectBytesAsync(s3key).getObjectContent();
//        return IOUtils.toByteArray(inputStream);
//    }

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


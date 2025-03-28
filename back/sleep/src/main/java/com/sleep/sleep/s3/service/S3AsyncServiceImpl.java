package com.sleep.sleep.s3.service;


import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.s3.constants.S3key;
import com.sleep.sleep.s3.dto.S3ObjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3AsyncServiceImpl implements S3AsyncService {
    private final S3Presigner presigner;
    private final JwtTokenUtil jwtTokenUtil;
    private final S3AsyncClient s3AsyncClient;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 유효 기간 내에 S3 객체를 조회할 수 있는 서명된 GET URL을 만들어서 반환합니다.
     * AWS에서 이 서명을 검사해서 정당한 요청인지, 유효 시간 안인지 판단합니다.
     * 외부 사용자가 직접 인증하지 않고도 한시적으로 객체 접근 권한을 부여받을 수 있습니다.
     *
     * @param accessToken 회원의 엑세스 토큰
     * @param s3key       S3 객체의 키
     * @return S3 객체를 조회할 수 있는 PresignedGetURL
     */
    @Override
    public String generatePresignedGetURL(String accessToken, String s3key) {
        String memberId = getMemberId(accessToken);
        return generatePresignedGetURL(s3key, Duration.ofMinutes(30));
    }

    /**
     * 유효 기간 내에 S3에 저장할 수 있는 서명된 PUT URL을 만들어서 반환합니다.
     * 생성할 S3 객체에 원본 쇼츠의 S3 key를 메타데이터로 저장합니다.
     *
     * @param accessToken 회원의 엑세스 토큰
     * @param createdAt   사용자 쇼츠 생성 날짜(파일 제목)
     * @param originS3key 원본 쇼츠 s3 key
     * @return S3에 저장할 수 있는 PresignedPutURL
     */
    @Override
    public String generatePresignedPutURL(String accessToken, String createdAt, String originS3key) {
        String memberId = getMemberId(accessToken);
        String userS3key = S3key.USERS.buildS3Key(memberId, createdAt);

        // 메타 데이터는 특수 문자 이슈 방지를 위해 Base64 인코딩함
        Map<String, String> metadata = new HashMap<>();
        String encodedOriginKey = Base64.getEncoder().encodeToString(originS3key.getBytes(StandardCharsets.UTF_8));
        metadata.put(S3key.ORIGIN.getFolder(), encodedOriginKey);

        return generatePresignedPutURL(userS3key, metadata, Duration.ofMinutes(10));
    }

    /**
     * 사용자 녹화한 쇼츠 목록을 조회합니다.
     *
     * @param accessToken 회원의 엑세스 토큰
     * @return S3ObjectDTO 객체 리스트
     */
    @Override
    public List<S3ObjectDto> getRecordedShortsList(String accessToken) {
        String memberId = getMemberId(accessToken);
        // S3의 lambda/{userId}/ 경로에 존재하는 객체들을 조회
        String prefix = S3key.LAMBDA.buildPrefix(memberId);

        return getS3ObjectList(prefix, Duration.ofMinutes(30));
    }

    /**
     * S3 객체 GET 요청을 생성하고, S3 객체 GET 요청의 서명된 URL을 생성합니다.
     *
     * @param s3key S3 객체의 키
     * @param valid PresignedGetURL 유효 기간
     * @return 요청 객체를 바탕으로 생성된 PresignedGetURL
     */
    public String generatePresignedGetURL(String s3key, Duration valid) {
        GetObjectRequest getObjectRequest = buildGetObjectRequest(s3key);
        GetObjectPresignRequest getObjectPresignRequest = buildGetObjectPresignRequest(getObjectRequest, valid);
        return presigner.presignGetObject(getObjectPresignRequest)
                .url()
                .toExternalForm();
    }

    /**
     * S3 객체 PUT 요청을 생성하고, S3 객체 PUT 요청의 서명된 URL을 생성합니다.
     * 프론트엔드에서 전송하는 메타데이터나 Content-Type이 이 요청에서 정의한 값과 일치하지 않을 경우, 업로드가 거부됩니다.
     *
     * @param userS3key 저장할 쇼츠의 S3 경로
     * @param metadata  쇼츠와 함께 저장할 메타데이터(원본 쇼츠 S3 경로)
     * @param valid     PresignedPutURL 유효 기간
     * @return 요청 객체를 바탕으로 생성된 PresignedPutURL
     */
    private String generatePresignedPutURL(String userS3key, Map<String, String> metadata, Duration valid) {
        PutObjectRequest putObjectRequest = buildPutObjectRequest(metadata, userS3key);
        PutObjectPresignRequest putObjectPresignRequest = buildPutObjectPresignRequest(putObjectRequest, valid);
        return presigner.presignPutObject(putObjectPresignRequest)
                .url()
                .toExternalForm();
    }

    /**
     * S3 prefix 경로에 해당하는 객체 리스트를 비동기 방식으로 조회합니다.
     * 최신순으로 정렬하여 Presigned URL을 포함한 DTO로 변환합니다.
     *
     * @param prefix S3 폴더 경로
     * @param valid  유효 기간
     * @return S3ObjectDto 객체 리스트
     */
    private List<S3ObjectDto> getS3ObjectList(String prefix, Duration valid) {
        ListObjectsV2Request listObjectsV2Request = buildListObjectsV2Request(prefix);
        return s3AsyncClient.listObjectsV2(listObjectsV2Request) // 비동기로 S3 객체 리스트 조회
                .thenApply(res -> // 성공하면 동기 후속 작업 실행
                        res.contents().stream()
                                .sorted(Comparator.comparing(S3Object::lastModified).reversed()) // lastModified 기준 역순 조회
                                .map(this::buildS3ObjectDto)
                                .toList()
                ).join();  // 결과가 준비되면 동기적으로 반환

    }

    /**
     * S3에서 객체를 가져오기 위한 요청 객체를 생성합니다.
     *
     * @param s3key S3 객체의 키
     * @return GetObjectRequest 객체
     */
    private GetObjectRequest buildGetObjectRequest(String s3key) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3key)
                .build();

    }

    /**
     * Presigned GET URL 생성을 위한 요청 정보를 담은 객체를 생성합니다.
     *
     * @param getObjectRequest S3 객체 GET 요청
     * @param valid            PresignedGetURL 유효 기간
     * @return GetObjectPresignRequest 객체
     */
    private GetObjectPresignRequest buildGetObjectPresignRequest(GetObjectRequest getObjectRequest, Duration valid) {
        return GetObjectPresignRequest.builder()
                .signatureDuration(valid) // 유효 기간
                .getObjectRequest(getObjectRequest)
                .build();

    }

    /**
     * S3에 객체를 저장하기 위한 요청 객체를 생성합니다.
     * 버킷 이름, 파일을 저장할 경로, 메타 데이터, 파일 종류가 포함됩니다.
     *
     * @param metadata  쇼츠와 함께 저장할 메타데이터(원본 쇼츠 S3 경로)
     * @param userS3key 저장할 쇼츠의 S3 경로
     * @return PutObjectRequest 객체
     */
    private PutObjectRequest buildPutObjectRequest(Map<String, String> metadata, String userS3key) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(userS3key)
                .metadata(metadata)
                .contentType("video/mp4")
                .build();
    }

    /**
     * Presigned PUT URL 생성을 위한 요청 정보를 담은 객체를 생성합니다.
     *
     * @param putObjectRequest S3 객체 PUT 요청
     * @param valid            PresignedPutURL 유효 기간
     * @return PutObjectPresingRequest 객체
     */
    private PutObjectPresignRequest buildPutObjectPresignRequest(PutObjectRequest putObjectRequest, Duration valid) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(valid) // 유효 기간
                .putObjectRequest(putObjectRequest)
                .build();

    }

    /**
     * S3 객체 리스트 요청을 위한 객체를 생성합니다.
     *
     * @param prefix S3 폴더 경로
     * @return ListObjectsV2Request 객체
     */
    private ListObjectsV2Request buildListObjectsV2Request(String prefix) {
        return ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
    }

    /**
     * S3 객체의 key를 활용하여 Presigned Get URL을 생성한 후에 반환합니다.
     *
     * @param s3Object S3 객체
     * @return S3ObjectDto 객체
     */
    private S3ObjectDto buildS3ObjectDto(S3Object s3Object) {
        return S3ObjectDto.builder()
                .key(s3Object.key())
                .url(generatePresignedGetURL(s3Object.key(), Duration.ofMinutes(30)))
                .size(s3Object.size())
                .lastModified(s3Object.lastModified())
                .build();
    }

    private String getMemberId(String accessToken) {
        return jwtTokenUtil.getUsername(accessToken.substring(7));
    }

}


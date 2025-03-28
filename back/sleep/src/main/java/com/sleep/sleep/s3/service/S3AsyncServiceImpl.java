package com.sleep.sleep.s3.service;


import com.sleep.sleep.common.JWT.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3AsyncServiceImpl implements S3AsyncService {
    private final S3Presigner presigner;
    private final S3AsyncClient s3AsyncClient;
    private final JwtTokenUtil jwtTokenUtil;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 유효 기간 내에 S3 객체를 조회할 수 있는 서명된 URL을 만들어서 반환합니다.
     * AWS에서 이 서명을 검사해서 정당한 요청인지, 유효 시간 안인지 판단합니다.
     * 외부 사용자가 직접 인증하지 않고도 한시적으로 객체 접근 권한을 부여받을 수 있습니다.
     *
     * @param accessToken 회원의 엑세스 토큰
     * @param s3Key       S3 객체의 키
     * @return S3 객체의 PresignedGetURL
     */
    @Override
    public String generatePresignedGetURL(String accessToken, String s3Key) {
        String memberId = getMemberId(accessToken);
        return generatePresignedGetURL(s3Key, Duration.ofMinutes(10));
    }

    /**
     * S3 객체 요청, S3 객체의 서명된 URL 요청을 생성합니다.
     * 생성된 요청을 바탕으로 주어진 유효 기간 동안만 객체를 조회할 수 있는 서명된 URL을 생성합니다.
     *
     * @param s3Key S3 객체의 키
     * @param valid PresignedURL 유효 기간
     * @return PresignedURL
     */
    private String generatePresignedGetURL(String s3Key, Duration valid) {
        GetObjectRequest getObjectRequest = buildGetObjectRequest(s3Key);
        GetObjectPresignRequest getObjectPresignRequest = buildGetObjectPresignRequest(getObjectRequest, valid);
        return presigner.presignGetObject(getObjectPresignRequest)
                .url()
                .toExternalForm();
    }

    /**
     * S3에서 객체를 가져오기 위한 요청 객체를 생성합니다.
     *
     * @param s3Key S3 객체의 키
     * @return GetObjectRequest 객체
     */
    private GetObjectRequest buildGetObjectRequest(String s3Key) {
        return GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

    }

    /**
     * Presigned URL 생성을 위한 요청 정보를 담은 객체를 생성합니다.
     *
     * @param getObjectRequest S3 객체 요청
     * @param valid            유효 기간
     * @return GetObjectPresignRequest 객체
     */
    private GetObjectPresignRequest buildGetObjectPresignRequest(GetObjectRequest getObjectRequest, Duration valid) {
        return GetObjectPresignRequest.builder()
                .signatureDuration(valid) // 유효 기간
                .getObjectRequest(getObjectRequest)
                .build();

    }

    private String getMemberId(String accessToken) {
        return jwtTokenUtil.getUsername(accessToken.substring(7));
    }

}


package com.sleep.sleep.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;
    private final MemberService memberService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 버킷 이름과 Key로 해당 S3 객체를 제공함
     *
     * @param s3key
     * @return S3 객체
     * @throws CustomException S3 객체를 찾을 수 없음
     */
    @Override
    public S3Object findS3Object(String s3key) {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, s3key));
        if (s3Object == null) throw new CustomException(ExceptionCode.S3OBJECT_NOT_FOUND);
        return s3Object;
    }

    /**
     * 버킷 이름과 Key로 해당 S3 객체의 URL을 제공함
     *
     * @param s3key S3 객체의 Key
     * @return S3 객체 URL
     */
    @Override
    public String findS3URL(String s3key) {
        findS3Object(s3key);
        return amazonS3.getUrl(bucketName, s3key).toString();
    }

    /**
     * 특정 S3 key로 얻은 객체를 스트림 형태로 받아서 바이트 배열(Blob)로 변환
     *
     * @param s3key 특정 S3 객체의 key
     * @return s3 객체의 스트림
     * @throws IOException 스트림을 바이트 배열로 변환하는 데 실패함
     */
    @Override
    public byte[] findBlobOfS3Object(String s3key) throws IOException {
        InputStream inputStream = findS3Object(s3key).getObjectContent();
        return IOUtils.toByteArray(inputStream);
    }

    /**
     * 회원이 녹화한 쇼츠를 S3에 업로드하고 해당 S3 객체의 Key를 반환함
     *
     * @param accessToken 로그인한 회원의 token
     * @param file        회원이 녹화한 쇼츠 파일(Blob 형식)
     * @return 업로드된 S3 객체의 key
     * @throws CustomException 파일 매개변수가 비어있음
     * @throws IOException     파일의 스트림을 여는 데 실패함
     */
    @Transactional
    @Override
    public String addRecordedShortsToS3(String accessToken, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new CustomException(ExceptionCode.RECORDED_FILE_NULL);
        Member member = memberService.findMemberEntity(accessToken);

        // s3에 업로드 하기 전에 파일의 메타데이터를 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        // 랜덤으로 파일 이름(제목)을 생성하고 회원 아이디(폴더명)에 붙임
        UUID uuid = UUID.randomUUID();
        String fileName = member.getMemberId() + "/" + uuid;

        // 파일의 InputStream을 s3에 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata));

        // 업로드된 S3 객체(파일)의 Key
        return findS3Object(fileName).getKey();
    }

    /**
     * 해당 key에 해당하는 S3 객체를 삭제
     *
     * @param s3key S3 객체의 key
     * @return 삭제에 성공하면 SuccessResponse 객체를 반환함
     */
    @Override
    public SuccessResponse deleteRecordedShortsFromS3(String s3key) {
        findS3Object(s3key);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, s3key));
        return SuccessResponse.of("회원이 녹화한 쇼츠가 S3에서 삭제되었습니다.");
    }


}

package com.sleep.sleep.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.sleep.sleep.member.repository.MemberRepository;
import com.sleep.sleep.shorts.service.ShortsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String getPath(String fileName) {
        return amazonS3.getUrl(bucketName, fileName).toString();
    }
//
//    public String uploadFile(MultipartFile multipartFile, String fileName, String username) throws IOException {
//        File file = convertMultiPartFileToFile(multipartFile);
//        String inputFileName=username+"/"+fileName;
//        log.info("inputFileName"+inputFileName);
//        amazonS3.putObject(new PutObjectRequest(bucketName, inputFileName, file));
//
//        String s3Url = amazonS3.getUrl(bucketName, inputFileName).toString();
//        RecordedShortsDto dto = new RecordedShortsDto(s3Url,inputFileName);
//        shortsService.upload(dto,username);
//
//        file.delete();
//        return s3Url;
//    }
//
//    public void deleteFile(int uploadNo,String fileName) {
//        amazonS3.deleteObject(new DeleteObjectRequest(bucketName,fileName));
//        shortsService.deleteUploadShorts(uploadNo,fileName);
//    }
//
//    private File convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException {
//        File file = new File(multipartFile.getOriginalFilename());
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            fos.write(multipartFile.getBytes());
//        }
//        return file;
//    }
//
//    public InputStream downloadFile(String filePath) {
//        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, filePath));
//        return s3Object.getObjectContent();
//    }
//
//    public void reaname(int uploadNo,String oldTitle, String newTitle) {
//        // 객체 복사 요청 생성
//        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, oldTitle, bucketName, newTitle);
//        // 객체 복사 수행
//        amazonS3.copyObject(copyObjectRequest);
//        // 기존 객체 삭제
//        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, oldTitle));
//        // 새로운 URL 반환
//        String newURL =  getPath(newTitle);
//
//        shortsService.putTitle(uploadNo,oldTitle, newTitle, newURL);
//
//    }
}

package com.sleep.sleep.s3;

import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.repository.UploadShortsRepository;
import com.sleep.sleep.shorts.service.ShortsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    private final S3Service s3Service;
    private final ShortsService shortsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UploadShortsRepository uploadShortsRepository;

    /**
     * multipartFile을 File로 변환한다.
     *
     * @param MultipartFile file 멀티파트 파일
     * @return File 변환된 파일을 반환한다.
     * @throws IOException
     */
    public static File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 임시 디렉토리에 파일 생성
        File convFile = File.createTempFile("upload-", "-" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }

    @Operation(summary = "동영상 업로드", description = "추후 사용자별 업로드로 수정 예정; param: 업로드할 파일이름")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestHeader("Authorization") String accessToken, @RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        File convFile = null;
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));

            System.out.println("username : " + username);

            convFile = multipartFileToFile(file); // 멀티파트 파일 -> 파일로 형변환

            double shortsTime = DurationExtractor.extract(convFile);    // 초 단위로 영상 길이 추출

            String uploadUrl = s3Service.uploadFile(file, fileName, username);
            return new ResponseEntity<String>(uploadUrl, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (convFile != null && convFile.exists()) {
                boolean isDeleted = convFile.delete();
                if (!isDeleted) {
                    System.err.println("Failed to delete the temporary file: " + convFile.getAbsolutePath());
                }
            }
        }
    }

    @Operation(summary = "동영상 링크 보기", description = "추후 사용자별 다운로드로 수정 예정; param: 다운로드할 파일이름")
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@RequestHeader("Authorization") String accessToken, @PathVariable String fileName) {
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            System.out.println("username : " + username);

            String filePath = s3Service.getPath(username + "/" + fileName);
            return new ResponseEntity<String>(filePath, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Operation(summary = "동영상 삭제", description = "uploadNo, title")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data) {
        try {
            int uploadNo = Integer.parseInt(data.get("uploadNo"));
            String fileName = data.get("title");

            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            System.out.println("username : " + username);

            s3Service.deleteFile(uploadNo, fileName);
            return new ResponseEntity<String>("Successfully delete!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String resolveToken(String accessToken) {
        log.info("resolveToken, AccessToken: " + accessToken);
        return accessToken.substring(7);
    }

    @Operation(summary = "동영상 파일 이름 변경", description = "헤더에 accessToken넣기. requestBody에 oldTitle, newTitle 이름 넣기")
    @PutMapping("/rename/{uploadNo}")
    public ResponseEntity<?> updateTitle(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data, @PathVariable int uploadNo) {
        try {
            System.out.println(data);

            //int uploadNo = Integer.parseInt(data.get("uploadNo"));
            String oldTitle = data.get("oldTitle");
            String newTitle = data.get("newTitle");


            // oldTitle과 newTitle 사용
            //System.out.println("Old Title: " + oldTitle);
            //System.out.println("New Title: " + newTitle);

            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));

            //s3와 db 업데이트하는 것
            s3Service.reaname(uploadNo, oldTitle, username + "/" + newTitle);

            return new ResponseEntity<String>("Successfully update!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 파일 로컬 저장
    @PostMapping("/save/{uploadNo}")
    public ResponseEntity<?> getLocalFilePath(@RequestHeader("Authorization") String accessToken, @PathVariable int uploadNo) {
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            //String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            System.out.println(username);

            RecordedShorts recordedShorts = uploadShortsRepository.findByUploadNo(uploadNo);

            // S3에서 파일 다운로드
            InputStream inputStream = s3Service.downloadFile(recordedShorts.getUploadTitle());

            // 임시 파일 생성
            File tempFile = File.createTempFile("downloaded-", ".mp4", new File(System.getProperty("java.io.tmpdir")));
            //tempFile.deleteOnExit();  // 프로그램 종료 시 파일 삭제

            // 파일로 스트림 복사
            Files.copy(inputStream, Paths.get(tempFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

            // 블롭(바이트 배열) 반환
            return ResponseEntity.ok()
                    .body(tempFile.getAbsolutePath());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // S3 파일 Blob 변환(원본 쇼츠 폴더)
    @PostMapping("/bring/blob/{shortsNo}")
    public ResponseEntity<?> getS3Blob(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsNo) {
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            //String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            String fileName = shortsService.getShortsInfo(shortsNo).getMusicName();
            System.out.println("fileName"+fileName);
            // S3에서 파일 다운로드
            InputStream inputStream = s3Service.downloadFile("Shorts/" + fileName);

            // InputStream을 바이트 배열로 변환
            byte[] fileContent = IOUtils.toByteArray(inputStream);

            // 블롭(바이트 배열) 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // S3 파일 blob 변환(사용자 폴더)
    @PostMapping("/bring/myblob/{uploadNo}")
    public ResponseEntity<?> getUserS3Blob(@RequestHeader("Authorization") String accessToken, @PathVariable int uploadNo) {
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            //String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

            // 업로드 쇼츠 db에서 찾기
            RecordedShorts recordedShorts = uploadShortsRepository.findByUploadNo(uploadNo);

            // S3에서 파일 다운로드
            InputStream inputStream = s3Service.downloadFile(recordedShorts.getUploadTitle());

            // InputStream을 바이트 배열로 변환
            byte[] fileContent = IOUtils.toByteArray(inputStream);

            // 블롭(바이트 배열) 반환
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
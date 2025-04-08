package com.sleep.sleep.s3.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3Status {
    PENDING("PENDING"), // 서명된 PUT URL 발급 완료
    UPLOADED("UPLOADED"), // S3 Users 폴더에 업로드 완료
    COMPLETED("COMPLETED"), // 음악 프로세싱 거치고 S3 Lambda 폴더에 업로드 완료
    FAILED("FAILED"); // S3 업로드 혹은 Lambda 처리 실패

    private String status;

}

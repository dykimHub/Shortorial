package com.sleep.sleep.s3.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3key {
    ORIGIN("origin/"),
    USERS("users/"),
    LAMBDA("lambda/");

    private String folder;

    public String buildPrefix(String memberId) {
        return this.folder + memberId + "/";
    }

    public String buildS3key(String memberId, String fileName) {
        return this.folder + memberId + "/" + fileName + ".mp4";
    }


}

package com.sleep.sleep.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;

public interface S3Service {

    String getPath(String folderName, String fileName);

    ObjectMetadata getObjectMetaData(String folderName, String fileName);

}

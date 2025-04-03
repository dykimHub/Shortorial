package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.s3.constants.S3Status;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordedShortsRepository extends JpaRepository<RecordedShorts, Integer> {

    @Query("SELECT r FROM RecordedShorts r WHERE r.recordedShortsS3key = :recordedShortsS3key")
    RecordedShorts findByRecordedShortsS3key(String recordedShortsS3key);

    @Modifying
    @Query("UPDATE RecordedShorts r SET r.status = :status WHERE r.recordedShortsS3key = :recordedShortsS3key")
    int modifyRecordedShortsStatus(String recordedShortsS3key, S3Status status);


}
package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.s3.constants.S3Status;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordedShortsRepository extends JpaRepository<RecordedShorts, Integer> {
    @Modifying
    @Query("UPDATE RecordedShorts r SET r.status = :status WHERE r.recordedShortsS3key = :recordedShortsS3key")
    int modifyRecordedShortsStatus(String recordedShortsS3key, S3Status status);

    @Modifying
    @Query("UPDATE RecordedShorts r SET r.recordedShortsTitle = :newRecordedShortsTitle WHERE r.recordedShortsId = :recordedShortsId")
    int modifyRecordedShortsTitle(int recordedShortsId, String newRecordedShortsTitle);

    @Modifying
    @Query("UPDATE RecordedShorts r SET r.isDeleted = true WHERE r.recordedShortsId = :recordedShortsId")
    int deleteByRecordedShortsId(int recordedShortsId);

    @Query("SELECT COUNT(*) > 0 FROM RecordedShorts r WHERE r.member.memberIndex = :memberIndex AND r.recordedShortsTitle = :recordedShortsTitle")
    boolean existsByRecordedShortsTitle(int memberIndex, String recordedShortsTitle);

}
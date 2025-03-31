package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.RecordedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordedShortsRepository extends JpaRepository<RecordedShorts, Integer> {

    @Query("SELECT r FROM RecordedShorts r WHERE r.member.memberIndex = :memberIndex ORDER BY r.recordedShortsDate DESC")
    Optional<List<RecordedShorts>> findByRecordedShortsList(int memberIndex);

    @Query("SELECT r FROM RecordedShorts r WHERE r.member.memberIndex = :memberId AND r.recordedShortsTitle = :newRecordedShortsTitle")
    Optional<RecordedShorts> findByRecordedShortsTitle(String memberId, String newRecordedShortsTitle);

    @Modifying
    @Query("UPDATE RecordedShorts r SET r.recordedShortsTitle = :newRecordedShortsTitle WHERE r.recordedShortsId = :recordedShortsId")
    void modifyRecordedShortsTitle(int recordedShortsId, String newRecordedShortsTitle);

    @Query("SELECT r FROM RecordedShorts r WHERE r.recordedShortsS3key = :S3key")
    Optional<RecordedShorts> findByRecordedShortsByS3key(String S3key);

}
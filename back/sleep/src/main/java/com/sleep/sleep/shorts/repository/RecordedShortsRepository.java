package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.RecordedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordedShortsRepository extends JpaRepository<RecordedShorts, Integer> {

    @Query("SELECT r FROM RecordedShorts r WHERE r.member.memberIndex = :memberIndex ORDER BY r.recordedShortsDate DESC")
    List<RecordedShorts> findByRecordedShortsList(int memberIndex);

    @Query("SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END FROM RecordedShorts r WHERE r.member.memberIndex = :memberIndex AND r.recordedShortsTitle = :newRecordedShortsTitle")
    boolean existsByRecordedShortsTitle(int memberIndex, String newRecordedShortsTitle);

    @Modifying
    @Query("UPDATE RecordedShorts r SET r.recordedShortsTitle = :newRecordedShortsTitle WHERE r.recordedShortsId = :recordedShortsId")
    void modifyRecordedShortsTitle(int recordedShortsId, String newRecordedShortsTitle);

}

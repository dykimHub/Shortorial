package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.TriedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TriedShortsRepository extends JpaRepository<TriedShorts,Integer> {

    @Query("SELECT t FROM TriedShorts t JOIN FETCH Member m WHERE m.memberId = :memberId")
    List<TriedShorts> findByMemberId(String memberId);

//    Optional<TryShorts> findByMemberIndexAndShortsNo(Member member, Shorts shorts);
//    @Query(nativeQuery = true, value = "select * from try_shorts where member_no = :memberNo and shorts_no=:shortsNo")
//    Optional<TriedShorts> findByMemberIndexAndShortsNo(@Param("memberNo") int memberNo, @Param("shortsNo") int shortsNo);
//
//    @Query(nativeQuery = true, value = "select * from try_shorts where member_no = :memberNo")
//    List<TriedShorts> findTryShortList(@Param("memberNo") int memberNo);
//
//    @Query("SELECT COUNT(ts) FROM TriedShorts ts WHERE ts.memberIndex.memberIndex = :memberIndex")
//    int countTryNoByMemberNo(@Param("memberIndex") int memberIndex);
}

package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriedShortsRepository extends JpaRepository<TriedShorts, Integer> {

    /**
     * 해당 회원이 시도한 TriedShorts 객체 리스트를 반환
     * Entity Graph로 데이터베이스 쿼리를 실행할 때 필요한 연관된 엔티티들을 미리 정의
     * TriedShorts 엔티티에서 shorts를 로드하고 그 shorts의 triedShortsList를 함께 로드
     * triedShortsList의 크기를 조회하는 쿼리를 객체마다 날리는 것을 방지
     *
     * @param member 특정 id의 회원
     * @return
     */
    @EntityGraph(attributePaths = {"shorts.triedShortsList"})
    @Query("SELECT t FROM TriedShorts t WHERE t.member = :member")
    List<TriedShorts> findTriedShortsList(Member member);

    @Query("SELECT t FROM TriedShorts t WHERE t.member = :member AND t.shorts = :shorts")
    TriedShorts findTriedShorts(Member member, Shorts shorts);

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

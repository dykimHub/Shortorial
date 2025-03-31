package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.TriedShorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TriedShortsRepository extends JpaRepository<TriedShorts, Integer> {

    /**
     * 해당 회원이 시도한 TriedShorts 객체 리스트를 반환
     * Entity Graph로 데이터베이스 쿼리를 실행할 때 필요한 연관된 엔티티들을 미리 정의
     * TriedShorts 엔티티에서 shorts를 로드하고 그 shorts의 triedShortsList를 함께 로드
     * triedShortsList의 크기를 조회하는 쿼리를 객체마다 날리는 것을 방지
     */
//    @EntityGraph(attributePaths = {"shorts.triedShortsList"})
//    @Query("SELECT t FROM TriedShorts t WHERE t.member = :member")
//    List<TriedShorts> findTriedShortsList(Member member);

    /**
     * 회원 id, 쇼츠 id 복합 unique key를 이용하여 회원이 시도한 쇼츠 객체를 반환
     *
     * @param memberId 특정 회원의 아이디
     * @param shortsId    특정 쇼츠의 id
     */
    @Query("SELECT t FROM TriedShorts t WHERE t.member.memberId = :memberId AND t.shorts.shortsId = :shortsId")
    Optional<TriedShorts> findTriedShorts(String memberId, int shortsId);

}

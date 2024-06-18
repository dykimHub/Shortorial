package com.sleep.sleep.shorts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QShorts qShorts = QShorts.shorts;
    private final QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

    /**
     * triedShortsList가 비어있는 Shorts 객체(TriedShorts에 없는 Shorts)도 가져오기 위해 left join 사용함
     * Shorts 객체의 triedShortsList를 tried_shorts 테이블과 fetch join 하여 shorts, tried_shorts 테이블을 한번의 쿼리로 불러옴
     * shorts_id로 그룹화하고 try_shorts_id가 많은 순서대로 내림차순 정렬함
     */
    @Override
    public List<Shorts> findPopularShorts() {
        return queryFactory.selectFrom(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts).fetchJoin()
                .groupBy(qShorts)
                .orderBy(qTriedShorts.count().desc())
                .limit(3)
                .fetch();

    }

    /**
     * 특정 id의 회원과 특정 id의 쇼츠로 만든 TriedShorts 객체가 이미 있는지 확인함
     *
     * @param member 특정 id의 회원
     * @param shorts 특정 id의 쇼츠
     */
    @Override
    public TriedShorts findTriedShorts(Member member, Shorts shorts) {
        return queryFactory.selectFrom(qTriedShorts)
                .where(qTriedShorts.member.eq(member)
                        .and(qTriedShorts.shorts.eq(shorts)))
                .fetchOne();

    }

//    /**
//     * 1. TriedShorts 리스트를 member로 필터링
//     * 2. 필터링된 TriedShorts 객체의 shorts를 Shorts와 innerJoin해서 해당 회원이 시도한 쇼츠의 정보만 불러옴
//     * 3. 조인된 Shorts 객체의 triedShortsList를 triedShorts와 innerJoin해서 조인된 쇼츠가 시도된 횟수까지 한 쿼리에 불러옴
//     *
//     * @param member 특정 id의 회원
//     */
//    @Override
//    public List<TriedShorts> findTriedShortsList(Member member) {
//        // 동일한 객체를 사용할 때 동일한 변수명은 못쓰고 alias를 지정해야 함
//        QTriedShorts qJoinedTriedShorts = new QTriedShorts("JoinedTriedShorts");
//
//        return queryFactory.selectFrom(qTriedShorts)
//                .where(qTriedShorts.member.eq(member))
//                .innerJoin(qTriedShorts.shorts, qShorts).fetchJoin()
//                .innerJoin(qShorts.triedShortsList, qJoinedTriedShorts).fetchJoin()
//                .fetch();
//    }
}

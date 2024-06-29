package com.sleep.sleep.shorts.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.dto.QShortsStatsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.entity.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QShorts qShorts = QShorts.shorts;
    private final QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
    private final QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;
    private final QMember qMember = QMember.member;

    /**
     * triedShortsList가 비어있는 Shorts 객체(TriedShorts에 없는 Shorts)도 가져오기 위해 left join 사용함
     * Shorts 객체의 triedShortsList를 tried_shorts 테이블과 fetch join 하여 shorts, tried_shorts 테이블을 한번의 쿼리로 불러옴
     * Shorts 객체의 triedShortsList의 사이즈가 큰 순서대로 정렬함
     * triedShortsList의 사이즈를 계산할 때 각 쇼츠 id를 triedShorts 테이블에서 count 하는 서브 쿼리를 날림
     */
    @Override
    public List<Shorts> findPopularShorts() {
        return queryFactory.selectFrom(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts).fetchJoin()
                .orderBy(qShorts.triedShortsList.size().desc())
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

    /**
     * 1. TriedShorts 리스트를 member로 필터링
     * 2. 필터링된 TriedShorts 객체의 shorts를 Shorts와 innerJoin해서 해당 회원이 시도한 쇼츠의 정보만 불러옴
     * 3. 조인된 Shorts 객체의 triedShortsList를 triedShorts와 innerJoin해서 조인된 쇼츠가 시도된 횟수까지 한 쿼리에 불러옴
     *
     * @param member 특정 id의 회원
     */
    @Override
    public List<TriedShorts> findTriedShortsList(Member member) {
        // 동일한 객체를 사용할 때 동일한 변수명은 못쓰고 alias를 지정해야 함
        QTriedShorts qJoinedTriedShorts = new QTriedShorts("JoinedTriedShorts");

        return queryFactory.selectFrom(qTriedShorts)
                .where(qTriedShorts.member.eq(member))
                .innerJoin(qTriedShorts.shorts, qShorts).fetchJoin()
                .innerJoin(qShorts.triedShortsList, qJoinedTriedShorts).fetchJoin()
                .orderBy(qTriedShorts.triedShortsDate.desc())
                .fetch();
    }

    /**
     * 서브쿼리로 tried_shorts, recorded_shorts의 member_id가 해당 member_id인 행 개수, recorded_shorts에서 youtubeUrl이 있는 행 개수를 불러옴
     * Q class로 만든 ShortsStateDto 객체에 매핑함; QMemeber가 아닌 다른 객체로 반환할 수 있음
     *
     * @param member 특정 id의 회원
     */
    @Override
    public ShortsStatsDto findShortsStats(Member member) {
        return queryFactory
                .select(new QShortsStatsDto(
                        JPAExpressions.select(qTriedShorts.count().intValue())
                                .from(qTriedShorts)
                                .where(qTriedShorts.member.eq(member)),
                        JPAExpressions.select(qRecordedShorts.count().intValue())
                                .from(qRecordedShorts)
                                .where(qRecordedShorts.member.eq(member)),
                        JPAExpressions.select(qRecordedShorts.count().intValue())
                                .from(qRecordedShorts)
                                .where(qRecordedShorts.member.eq(member)
                                        .and(qRecordedShorts.recordedShortsYoutubeUrl.isNotNull()))
                ))
                .from(qMember)
                .fetchOne();

    }


}

package com.sleep.sleep.shorts.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.entity.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * triedShortsList가 비어있는 Shorts 객체(TriedShorts에 없는 Shorts)도 가져오기 위해 left join 사용함
     * Shorts 객체의 triedShortsList를 tried_shorts 테이블과 fetch join 하여 shorts, tried_shorts 테이블을 한번의 쿼리로 불러옴
     * Shorts 객체의 triedShortsList의 사이즈가 큰 순서대로 정렬함
     * triedShortsList의 사이즈를 계산할 때 각 쇼츠 id를 triedShorts 테이블에서 count 하는 서브 쿼리를 날림
     */
    @Override
    public List<Shorts> findPopularShorts() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

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
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

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
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        // 동일한 객체를 사용할 때 동일한 변수명은 못쓰고 alias를 지정해야 함
        QTriedShorts qJoinedTriedShorts = new QTriedShorts("JoinedTriedShorts");

        return queryFactory.selectFrom(qTriedShorts)
                .innerJoin(qTriedShorts.shorts, qShorts).fetchJoin()
                .innerJoin(qShorts.triedShortsList, qJoinedTriedShorts).fetchJoin()
                .where(qTriedShorts.member.eq(member))
                //.orderBy(qTriedShorts.triedShortsDate.desc())
                .fetch();
    }

    /**
     * 1. TriedShorts 리스트를 member로 필터링하여 카운트 함
     * 2. RecordedShorts 리스트를 member로 필터링하여 카운트하고, YoutubeURL이 있는 객체는 별도로 카운트하여 튜플로 반환함
     * 3. ShortsStatsDto 객체를 생성할 때 반영함
     * 복수 쿼리라서 일관성이 부족할 수 있음
     * 서브 쿼리 방식과 달리 member DB를 조회하지 않아도 되고, 녹화된 쇼츠 DB를 2번 조회하지 않아도 됨
     *
     * @param member 특정 id의 회원
     */
    @Override
    public ShortsStatsDto findShortsStatsDto(Member member) {
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;
//        QMember qMember = QMember.member;

        int triedShortsNum = queryFactory.select(qTriedShorts.count().intValue())
                .from(qTriedShorts)
                .where(qTriedShorts.member.eq(member))
                .fetchOne();

        Tuple tuple = queryFactory.select(
                        qRecordedShorts.count().intValue(),
                        qRecordedShorts.recordedShortsYoutubeURL.isNotNull().count().intValue())
                .from(qRecordedShorts)
                .where(qRecordedShorts.member.eq(member))
                .fetchOne();

        return ShortsStatsDto.builder()
                .triedShortsNum(triedShortsNum)
                .recordedShortsNum(tuple.get(0, Integer.class))
                .uploadedShortsNum(tuple.get(1, Integer.class))
                .build();

//        Q class로 만든 ShortsStateDto 객체에 매핑함; QMemeber가 아닌 다른 객체로 반환할 수 있음
//        각 테이블을 서브 쿼리로 조회해서 단일 쿼리로 불러오는 방식으로 일관성을 유지하고 직접 프로젝션이 가능함
//        SQL 문법 상으로는 from, where 절이 필요없는데 QueryDSL 문법 상 필요해서 불필요한 로직을 수행함

//        return queryFactory
//                .select(new QShortsStatsDto(
//                        JPAExpressions.select(qTriedShorts.count().intValue())
//                                .from(qTriedShorts)
//                                .where(qTriedShorts.member.eq(member)),
//                        JPAExpressions.select(qRecordedShorts.count().intValue())
//                                .from(qRecordedShorts)
//                                .where(qRecordedShorts.member.eq(member)),
//                        JPAExpressions.select(qRecordedShorts.count().intValue())
//                                .from(qRecordedShorts)
//                                .where(qRecordedShorts.member.eq(member)
//                                        .and(qRecordedShorts.recordedShortsYoutubeURL.isNotNull()))
//                ))
//                .from(qMember)
//                .where(qMember.eq(member))
//                .fetchOne();


    }


}

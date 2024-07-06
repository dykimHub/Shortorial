package com.sleep.sleep.shorts.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.QRecordedShorts;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    /**
     * QueryProjection으로 QShortsDto를 만들어서 FROM(Shorts) 이지만 ShortsDto 객체에 매핑함
     * triedShortsList의 크기를 조회하는 서브 쿼리를 보내고(tried_shorts 테이블에서 shorts_id를 count) ShortsDto의 shortsChallengerNum을 계산함
     *
     * @param shorts 특정 id의 쇼츠
     */
    @Override
    public ShortsDto findShorts(Shorts shorts) {
        QShorts qShorts = QShorts.shorts;

        return queryFactory.select(new QShortsDto(
                        qShorts.shortsId,
                        qShorts.shortsTime,
                        qShorts.shortsTitle,
                        qShorts.shortsMusicTitle,
                        qShorts.shortsMusicSinger,
                        qShorts.shortsSource,
                        qShorts.shortsS3Link,
                        qShorts.triedShortsList.size()))
                .from(qShorts)
                .where(qShorts.eq(shorts))
                .fetchOne();

    }

    /**
     * triedShortsList가 비어있는 Shorts 객체(TriedShorts에 없는 Shorts)도 가져오기 위해 left join 사용함
     * 각 Shorts를 기준으로 group한 TriedShorts를 카운트 해서 shortsChallengerNum을 계산함
     * Shorts 객체 한 개를 반환할 때는 서브 쿼리를 사용했지만 전체 ShortsList를 조회할 때는 Join을 통해 각 행마다 서브 쿼리를 보내는 것을 방지
     */
    @Override
    public List<ShortsDto> findShortsList() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        return queryFactory.select(new QShortsDto(
                        qShorts.shortsId,
                        qShorts.shortsTime,
                        qShorts.shortsTitle,
                        qShorts.shortsMusicTitle,
                        qShorts.shortsMusicSinger,
                        qShorts.shortsSource,
                        qShorts.shortsS3Link,
                        qTriedShorts.count().intValue()))
                .from(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts)
                .groupBy(qShorts.shortsId)
                .fetch();

    }

    /**
     * findShortsList와 동일한 로직에서 TriedShorts를 카운트 한 열을 별칭 선언하고 정렬한 후 내림차순으로 3개 반환
     */
    @Override
    public List<ShortsDto> findPopularShortsList() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        // 별칭(alias) 선언해서 정렬 시 활용
        NumberPath<Integer> shortsChallengerNum = Expressions.numberPath(Integer.class, "shortsChallengerNum");

        return queryFactory.select(new QShortsDto(
                        qShorts.shortsId,
                        qShorts.shortsTime,
                        qShorts.shortsTitle,
                        qShorts.shortsMusicTitle,
                        qShorts.shortsMusicSinger,
                        qShorts.shortsSource,
                        qShorts.shortsS3Link,
                        qTriedShorts.count().intValue().as(shortsChallengerNum)))
                .from(qShorts)
                .leftJoin(qShorts.triedShortsList, qTriedShorts)
                .groupBy(qShorts.shortsId)
                .orderBy(shortsChallengerNum.desc())
                .limit(3)
                .fetch();

    }

    /**
     * 1. TriedShorts 리스트를 member로 필터링
     * 2. 필터링된 TriedShorts 객체의 shorts를 Shorts와 innerJoin해서 해당 회원이 시도한 쇼츠의 정보만 불러옴
     * 3. 쇼츠 정보 중 shortsChallengerNum을 구할 때 tried_shorts 테이블에서 shorts_id를 count하는 서브 쿼리를 보냄(qShorts.triedShortsList.size())
     * qShorts.triedShortsList와 새롭게 정의한 qTriedShorts를 한번 더 join해서 계산할 수도 있으나 서브 쿼리를 보내는 쪽이 성능이 좋았음
     *
     * @param member 특정 id의 회원
     */
    @Override
    public List<TriedShortsDto> findTriedShortsList(Member member) {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        // From절과 동일한 객체를 사용할 때 동일한 변수명은 못쓰고 alias를 지정해야 함
        // QTriedShorts qJoinedTriedShorts = new QTriedShorts("JoinedTriedShorts");

        return queryFactory.select(new QTriedShortsDto(
                        qTriedShorts.triedShortsId,
                        qTriedShorts.triedShortsDate,
                        new QShortsDto(
                                qShorts.shortsId,
                                qShorts.shortsTime,
                                qShorts.shortsTitle,
                                qShorts.shortsMusicTitle,
                                qShorts.shortsMusicSinger,
                                qShorts.shortsSource,
                                qShorts.shortsS3Link,
                                qShorts.triedShortsList.size()
                        )
                ))
                .from(qTriedShorts)
                .innerJoin(qTriedShorts.shorts, qShorts)
                .where(qTriedShorts.member.eq(member))
                .orderBy(qTriedShorts.triedShortsDate.desc())
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

//        각 테이블을 서브 쿼리로 조회해서 단일 쿼리로 불러오는 방식으로 일관성을 유지
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

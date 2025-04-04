package com.sleep.sleep.shorts.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.QRecordedShorts;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ShortsRepositoryCustomImpl implements ShortsRepositoryCustom {
    private final JPAQueryFactory queryFactory;

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
                        qShorts.shortsS3key,
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
                        qShorts.shortsS3key,
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
     * @param memberIndex 특정 회원의 번호
     */
    @Override
    public List<TriedShortsDto> findTriedShortsList(int memberIndex) {
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
                                qShorts.shortsS3key,
                                qShorts.triedShortsList.size()
                        )
                ))
                .from(qTriedShorts)
                .innerJoin(qTriedShorts.shorts, qShorts)
                .where(qTriedShorts.member.memberIndex.eq(memberIndex))
                .orderBy(qTriedShorts.triedShortsDate.desc())
                .fetch();

    }

    /**
     * 1. tried_shorts에서 해당 member index를 조회하고 count하는 서브쿼리를 보냄
     * 2. recorded_shorts에서 해당 member index를 조회하고 count하는 서브쿼리를 보냄
     * 3. recorded_shorts에서 해당 member index에 youtubeURL이 null이 아닌 행을 조회하고 count하는 서브쿼리를 보냄
     *
     * @param memberIndex 특정 회원 번호
     */
    @Override
    public Optional<ShortsStatsDto> findShortsStatsDto(int memberIndex) {
        QMember qMember = QMember.member;
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;

        return Optional.ofNullable(queryFactory.select(new QShortsStatsDto(
                        qMember.triedShortsList.size(),
                        qMember.recordedShortsList.size(),
                        JPAExpressions.select(qRecordedShorts.count().intValue())
                                .from(qRecordedShorts)
                                .where(qRecordedShorts.member.memberIndex.eq(memberIndex)
                                        .and(qRecordedShorts.recordedShortsYoutubeURL.isNotNull()))))
                .from(qMember)
                .where(qMember.memberIndex.eq(memberIndex))
                .fetchOne());

    }

    /**
     * 해당 회원 번호에 해당하는 녹화된 쇼츠 데이터를 조회합니다.
     * 녹화된 쇼츠 데이터 중 RecordedShortsDto에 해당하는 열만 매핑하여 리스트로 반환합니다.
     *
     * @param memberIndex 회원 번호
     * @return RecordedShortsDto 리스트
     */
    @Override
    public List<RecordedShortsDto> findRecordedShortsDtoList(int memberIndex) {
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;
        return queryFactory.select(new QRecordedShortsDto(
                        qRecordedShorts.recordedShortsId,
                        qRecordedShorts.recordedShortsTitle,
                        qRecordedShorts.recordedShortsDate,
                        qRecordedShorts.recordedShortsS3key,
                        qRecordedShorts.recordedShortsYoutubeURL,
                        qRecordedShorts.shorts.shortsMusicTitle,
                        qRecordedShorts.shorts.shortsMusicSinger
                ))
                .from(qRecordedShorts)
                // memberId가 아닌 다른 컬럼을 기준으로 조회하면 Lazy Fetch라고 하더라도 서브 쿼리 생성함
                .where(qRecordedShorts.member.memberIndex.eq(memberIndex))
                .fetch();
    }

}

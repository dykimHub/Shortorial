package com.sleep.sleep.shorts.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.s3.constants.S3Status;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.QRecordedShorts;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
     * 회원의 숏폼 통계 정보를 조회합니다.
     *
     * 1. tried_shorts에서 해당 memberIndex로 연습한 숏폼 개수를 조회
     * 2. recorded_shorts에서 해당 memberIndex로 녹화한 숏폼 개수를 조회
     * 3. tried_shorts 중 아직 recorded_shorts에 없는 최신 숏폼의 제목을 조회
     *
     * @param memberIndex 특정 회원 번호
     */
    @Override
    public ShortsStatsDto findShortsStatsDto(int memberIndex) {
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;

        Long triedShortsNum = queryFactory.select(qTriedShorts.count())
                .from(qTriedShorts)
                .where(qTriedShorts.member.memberIndex.eq(memberIndex))
                .fetchOne();

        Long recordedShortsNum = queryFactory.select(qRecordedShorts.count())
                .from(qRecordedShorts)
                .where(qRecordedShorts.member.memberIndex.eq(memberIndex))
                .fetchOne();

        String unRecordedShortsTitle = queryFactory.select(qTriedShorts.shorts.shortsMusicTitle)
                .from(qTriedShorts)
                .where(qTriedShorts.member.memberIndex.eq(memberIndex)
                        .and(JPAExpressions
                                .selectOne()
                                .from(qRecordedShorts)
                                .where(
                                        qRecordedShorts.shorts.shortsId.eq(qTriedShorts.shorts.shortsId)
                                                .and(qRecordedShorts.member.memberIndex.eq(memberIndex))
                                                .and(qRecordedShorts.status.eq(S3Status.COMPLETED))
                                )
                                .notExists()))
                .orderBy(qTriedShorts.triedShortsDate.desc())
                .limit(1)
                .fetchOne();

        return ShortsStatsDto.builder()
                .triedShortsNum(triedShortsNum.intValue())
                .recordedShortsNum(recordedShortsNum.intValue())
                .unRecordedShortsTitle(unRecordedShortsTitle)
                .build();

    }

    /**
     * 해당 회원 번호에 해당하고 상태가 COMPLETED인 녹화된 쇼츠 데이터를 조회합니다.
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
                .where(qRecordedShorts.member.memberIndex.eq(memberIndex)
                        .and(qRecordedShorts.status.eq(S3Status.COMPLETED)))
                .orderBy(qRecordedShorts.recordedShortsDate.desc())
                .fetch();
    }

}

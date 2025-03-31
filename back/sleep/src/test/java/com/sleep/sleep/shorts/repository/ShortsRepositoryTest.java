package com.sleep.sleep.shorts.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.entity.QMember;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.QRecordedShorts;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
class ShortsRepositoryTest {

    private final int memberIndex = 1;

    @Autowired
    private JPAQueryFactory queryFactory;

    @DisplayName("특정 회원의 TriedShorts 목록을 DTO 형식으로 조회")
    @Test
    void findTriedShortsList_DTOVer() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;

        List<TriedShortsDto> triedShortsDtoList = queryFactory.select(new QTriedShortsDto(
                        qTriedShorts.triedShortsId,
                        qTriedShorts.triedShortsDate,
                        new QShortsDto(
                                qShorts.shortsId,
                                qShorts.shortsTime,
                                qShorts.shortsTitle,
                                qShorts.shortsMusicTitle,
                                qShorts.shortsMusicSinger,
                                qShorts.shortsSource,
                                qShorts.triedShortsList.size()
                        )
                ))
                .from(qTriedShorts)
                .innerJoin(qTriedShorts.shorts, qShorts)
                .where(qTriedShorts.member.memberIndex.eq(memberIndex))
                .orderBy(qTriedShorts.triedShortsDate.desc())
                .fetch();

        assertNotNull(triedShortsDtoList);
        log.info("{}번 회원이 시도한 쇼츠 수: {}개", memberIndex, triedShortsDtoList.size());

    }

    @DisplayName("특정 회원의 TriedShorts 목록을 엔티티 형식으로 조회")
    @Test
    void findTriedShortsList_EntityVer() {
        QShorts qShorts = QShorts.shorts;
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        QTriedShorts qJoinedTriedShorts = new QTriedShorts("JoinedTriedShorts");

        List<TriedShorts> triedShortsList = queryFactory.selectFrom(qTriedShorts)
                .innerJoin(qTriedShorts.shorts, qShorts).fetchJoin()
                .innerJoin(qShorts.triedShortsList, qJoinedTriedShorts).fetchJoin()
                .where(qTriedShorts.member.memberIndex.eq(memberIndex))
                .orderBy(qTriedShorts.triedShortsDate.desc())
                .fetch();

        assertNotNull(triedShortsList);
        log.info("{}번 회원이 시도한 쇼츠 수: {}개", memberIndex, triedShortsList.size());

    }

    @DisplayName("쇼츠 통계 항목을 단일 쿼리로 계산")
    @Test
    void findShortsStatsDto_SingleQueryVer() {
        QMember qMember = QMember.member;
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;

        ShortsStatsDto shortsStatsDto = queryFactory.select(new QShortsStatsDto(
                        qMember.triedShortsList.size(),
                        qMember.recordedShortsList.size(),
                        JPAExpressions.select(qRecordedShorts.count().intValue())
                                .from(qRecordedShorts)
                                .where(qRecordedShorts.member.memberIndex.eq(memberIndex)
                                        .and(qRecordedShorts.recordedShortsYoutubeURL.isNotNull()))))
                .from(qMember)
                .where(qMember.memberIndex.eq(memberIndex))
                .fetchOne();


        log.info("{}번 회원의 쇼츠 통계 -> {}", memberIndex, shortsStatsDto);

    }

    @DisplayName("쇼츠 통계 항목을 개별 쿼리로 계산")
    @Test
    void findShortsStatsDto_IndividualQueryVer() {
        QTriedShorts qTriedShorts = QTriedShorts.triedShorts;
        QRecordedShorts qRecordedShorts = QRecordedShorts.recordedShorts;

        int triedShortsNum = queryFactory.select(qTriedShorts
                        .count().intValue())
                .from(qTriedShorts)
                .where(qTriedShorts.member.memberIndex.eq(memberIndex))
                .fetchOne();

        Tuple tuple = queryFactory.select(
                        qRecordedShorts.count().intValue(),
                        new CaseBuilder()
                                .when(qRecordedShorts.recordedShortsYoutubeURL.isNotNull())
                                .then(1)
                                .otherwise(0)
                                .sum().coalesce(0))
                .from(qRecordedShorts)
                .where(qRecordedShorts.member.memberIndex.eq(memberIndex))
                .fetchOne();

        ShortsStatsDto shortsStatsDto = ShortsStatsDto.builder()
                .triedShortsNum(triedShortsNum)
                .recordedShortsNum(tuple.get(0, Integer.class))
                .uploadedShortsNum(tuple.get(1, Integer.class))
                .build();

        log.info("{}번 회원의 쇼츠 통계 -> {}", memberIndex, shortsStatsDto);


    }


}
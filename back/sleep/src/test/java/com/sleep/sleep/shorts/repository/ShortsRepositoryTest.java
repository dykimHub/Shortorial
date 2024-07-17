package com.sleep.sleep.shorts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.shorts.dto.QShortsDto;
import com.sleep.sleep.shorts.dto.QTriedShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
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
                                qShorts.shortsS3Key,
                                qShorts.shortsS3URL,
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


}
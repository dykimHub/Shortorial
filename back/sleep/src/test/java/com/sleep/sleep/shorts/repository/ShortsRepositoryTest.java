package com.sleep.sleep.shorts.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.repository.MemberRepository;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.shorts.dto.QShortsDto;
import com.sleep.sleep.shorts.dto.QTriedShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.QShorts;
import com.sleep.sleep.shorts.entity.QTriedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.service.ShortsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@ActiveProfiles("test")
@SpringBootTest
class ShortsRepositoryTest {

    private final int memberIndex = 1;

    @Autowired
    MemberService memberService;
    @Autowired
    ShortsService shortsService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ShortsRepository shortsRepository;
    @Autowired
    TriedShortsRepository triedShortsRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    void setUp() {
        /**
         * 테스트 데이터베이스에 회원 1천 명 추가
         */
        for (int i = 1; i <= 1000; i++) {
            String memberId = "member" + i;
            String memberPass = "pass" + i;
            String memberNickname = "nickname" + i;

            JoinDto joinDto = new JoinDto(memberId, memberPass, memberNickname);
            memberService.join(joinDto);
        }

        /**
         * 5,000개의 랜덤한 TriedShorts 객체를 생성하여 테스트 데이터베이스에 추가
         */
        Random random;
        for (int i = 1; i <= 5000; i++) {
            random = new Random();

            int memberId = random.nextInt(1000) + 1;
            int shortsId = random.nextInt(15) + 1;

            Member member = memberRepository.findById(memberId).orElseThrow();
            Shorts shorts = shortsRepository.findById(shortsId).orElseThrow();

            TriedShorts newTriedShorts = TriedShorts.builder()
                    .member(member)
                    .shorts(shorts)
                    .build();

            triedShortsRepository.save(newTriedShorts);
        }
    }

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
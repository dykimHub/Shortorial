package com.sleep.sleep;

import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.repository.MemberRepository;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import com.sleep.sleep.shorts.service.ShortsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Random;
import java.util.UUID;

@Sql("/data.sql")
@ActiveProfiles("test")
@SpringBootTest
class SleepApplicationTest {

    private final int memberNum = 1000;
    private final int shortsNum = 15;
    private final Random random = new Random();
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
    RecordedShortsRepository recordedShortsRepository;

    @Test
    void setUp() {
        addMembers();
        addTriedShorts(5000);
        addRecordedShorts(5000);
    }

    private void addMembers() {
        for (int i = 1; i <= memberNum; i++) {
            String memberId = "member" + i;
            String memberPass = "pass" + i;
            String memberNickname = "nickname" + i;

            JoinDto joinDto = new JoinDto(memberId, memberPass, memberNickname);
            memberService.join(joinDto);
        }

    }

    private void addTriedShorts(int count) {
        for (int i = 1; i <= count; i++) {
            int memberIndex = random.nextInt(memberNum) + 1;
            Member member = memberRepository.findById(memberIndex).orElseThrow();

            int shortsId = random.nextInt(shortsNum) + 1;
            Shorts shorts = shortsRepository.findById(shortsId).orElseThrow();

            if (triedShortsRepository.findTriedShorts(member.getMemberIndex(), shorts.getShortsId()) != null) continue;

            TriedShorts newTriedShorts = TriedShorts.builder()
                    .member(member)
                    .shorts(shorts)
                    .build();

            triedShortsRepository.save(newTriedShorts);
        }

    }

    private void addRecordedShorts(int count) {
        for (int i = 1; i <= count; i++) {
            int memberIndex = random.nextInt(memberNum) + 1;
            Member member = memberRepository.findById(memberIndex).orElseThrow();

            RecordedShorts recordedShorts = RecordedShorts.builder()
                    .recordedShortsTitle(UUID.randomUUID().toString())
                    .recordedShortsS3key(UUID.randomUUID().toString())
                    .recordedShortsS3URL(UUID.randomUUID().toString())
                    .member(member)
                    .build();

            recordedShortsRepository.save(recordedShorts);

        }

    }

}
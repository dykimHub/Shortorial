package com.sleep.sleep;

import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.repository.MemberRepository;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import com.sleep.sleep.shorts.service.ShortsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Random;

@ActiveProfiles("test")
@SpringBootTest
class SleepApplicationTest {

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

}
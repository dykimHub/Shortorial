package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.service.S3AsyncServiceImpl;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortsServiceImpl implements ShortsService {
    private final MemberService memberService;
    private final ShortsRepository shortsRepository;
    private final TriedShortsRepository triedShortsRepository;
    private final RecordedShortsRepository recordedShortsRepository;

    private final S3AsyncServiceImpl s3AsyncServiceImpl;

    /**
     * 특정 id의 Shorts 객체를 ShortsDto 객체로 반환
     *
     * @param shortsId Shorts 객체의 id
     * @return ShortsDto 객체
     */
    @Override
    public ShortsDto findShortsDto(int shortsId) {
        Shorts shorts = findShorts(shortsId);
        return convertToShortsDto(shorts);

    }


    /**
     * Shorts DB의 모든 Shorts 객체를 ShortsDto 리스트로 반환함
     *
     * @return ShortsDto 리스트
     * @throws CustomException Shorts DB를 조회할 수 없음
     */
    @Override
    public List<ShortsDto> findShortsList() {
        return shortsRepository.findShortsList()
                .orElseThrow(() -> new CustomException(ExceptionCode.ALL_SHORTS_NOT_FOUND));

    }

    /**
     * Shorts 객체의 triedShortsList가 큰 순서대로 ShortDto형으로 바꿔서 3개를 반환함
     *
     * @return ShortsDto 객체 3개
     * @throws CustomException 인기 쇼츠 리스트를 불러올 수 없음
     */
    @Override
    public List<ShortsDto> findPopularShortsList() {
        return shortsRepository.findPopularShortsList()
                .orElseThrow(() -> new CustomException(ExceptionCode.POPULAR_SHORTS_NOT_FOUND));

    }

    /**
     * 특정 회원의 시도한 쇼츠 개수, 녹화한 쇼츠 개수, 업로드한 쇼츠 개수를 ShortsStatsDto 객체로 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return ShortsStatsDto 객체
     * @throws CustomException 회원의 쇼츠 통계를 불러올 수 없음
     */
    @Transactional
    @Override
    public ShortsStatsDto findShortsStats(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);

        return shortsRepository.findShortsStatsDto(member.getMemberIndex())
                .orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_STATS_NOT_FOUND));
    }

    /**
     * 특정 id의 회원의 TriedShorts 리스트를 TriedShortsDto 리스트로 반환함
     * With 어노테이션을 활용하여 triedShortsDate를 제외한 변수들은 기존 변수에서 복사함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return triedShortsDto 리스트
     */
    @Override
    public List<TriedShortsDto> findTriedShortsList(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);

        return shortsRepository.findTriedShortsList(member.getMemberIndex())
//                map 메서드는 Optinal 비어있으면 변환 작업을 수행하지 않음
//                각 시간대별 변환을 프론트 라이브러리가 처리
//                .map(tlist -> tlist.stream()
//                        .map(t -> t.withTriedShortsDate(t.getTriedShortsDate().plusHours(9)))
//                        .toList())
                .orElse(Collections.emptyList());

    }

    /**
     * 특정 id의 회원과 특정 id의 쇼츠로 만든 새로운 TriedShorts 객체를 tried_shorts 테이블에 등록함
     * TriedShorts 객체가 이미 있다면 TriedShorts 객체의 triedShortsDate를 현재 시간으로 업데이트 함
     *
     * @param accessToken 로그인한 회원의 token
     * @param shortsId    Shorts 객체의 id
     * @return TriedShorts 객체가 이미 있거나 등록에 성공하면 SuccessResponse 객체를 반환함
     */
    @Transactional
    @Override
    public SuccessResponse addTriedShorts(String accessToken, int shortsId) {
        Member member = memberService.findMemberEntity(accessToken);
        Shorts shorts = findShorts(shortsId);

        Optional<TriedShorts> triedShorts = triedShortsRepository.findTriedShorts(member.getMemberIndex(), shorts.getShortsId());
        if (triedShorts.isPresent()) {
            TriedShorts existingTriedShorts = triedShorts.get();
            existingTriedShorts.updateTriedShortsDate();
            triedShortsRepository.save(existingTriedShorts);
            return SuccessResponse.of("이미 시도한 쇼츠입니다. 시도한 날짜를 변경합니다.");
        } else {
            TriedShorts newTriedShorts = TriedShorts.builder()
                    .member(member)
                    .shorts(shorts)
                    .build();

            triedShortsRepository.save(newTriedShorts);
            return SuccessResponse.of("회원이 시도한 쇼츠에 추가되었습니다.");
        }
    }

    /**
     * 특정 id의 TriedShorts 객체를 삭제함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @param shortsId    Shorts 객체의 id
     * @return TriedShorts 삭제에 성공하면 SuccessResponse 객체를 반환함
     * @throws CustomException 해당 TriedShorts 객체를 찾을 수 없음
     */
    @Transactional
    @Override
    public SuccessResponse deleteTriedShorts(String accessToken, int shortsId) {
        Member member = memberService.findMemberEntity(accessToken);
        Shorts shorts = findShorts(shortsId);

        TriedShorts triedShorts = triedShortsRepository.findTriedShorts(member.getMemberIndex(), shorts.getShortsId())
                .orElseThrow(() -> new CustomException(ExceptionCode.TRIED_SHORTS_NOT_FOUND));

        triedShortsRepository.delete(triedShorts);

        return SuccessResponse.of("회원이 시도한 쇼츠에서 삭제되었습니다.");
    }

    /**
     * 특정 id의 Shorts 객체를 반환
     *
     * @param shortsId Shorts 객체의 id
     * @return Shorts 객체
     * @throws CustomException 해당 Shorts 객체를 찾을 수 없음
     */
    @Override
    public Shorts findShorts(int shortsId) {
        return shortsRepository.findById(shortsId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_NOT_FOUND));
    }

    /**
     * Shorts 객체를 ShortsDto 객체로 반환
     *
     * @param shorts Shorts 객체
     * @return ShortsDto 형으로 변환된 객체
     */
    @Override
    public ShortsDto convertToShortsDto(Shorts shorts) {
        return ShortsDto.builder()
                .shortsId(shorts.getShortsId())
                .shortsTime(shorts.getShortsTime())
                .shortsTitle(shorts.getShortsTitle())
                .shortsMusicTitle(shorts.getShortsMusicTitle())
                .shortsMusicSinger(shorts.getShortsMusicSinger())
                .shortsSource(shorts.getShortsSource())
                .shortsS3Key(shorts.getShortsS3Key())
                .shortsS3URL(s3AsyncServiceImpl.generatePresignedGetURL(shorts.getShortsS3Key(), Duration.ofMinutes(30)))
                // tried_shorts 테이블에서 shorts_id를 count하는 서브쿼리를 보냄
                .shortsChallengerNum(shorts.getTriedShortsList().size())
                .build();

    }


}

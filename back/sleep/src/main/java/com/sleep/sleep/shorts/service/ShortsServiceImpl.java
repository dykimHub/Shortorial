package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.service.S3AsyncService;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortsServiceImpl implements ShortsService {
    private final MemberService memberService;
    private final ShortsRepository shortsRepository;
    private final S3AsyncService s3AsyncService;

    /**
     * 쇼츠 DB에서 특정 id에 해당하는 쇼츠를 조회함
     * 쇼츠 제목을 활용하여 해당 쇼츠의 s3 key를 만들고 서명된 Get URL을 생성함
     * 쇼츠의 id, 제목과 생성된 S3 URL로 ShortsDTO를 생성하여 반환함
     *
     * @param shortsId 쇼츠 객체의 id
     * @return ShorsDTO 객체
     */
    @Override
    public ShortsDto findShortsDto(int shortsId) {
        Shorts shorts = findShorts(shortsId);
        String s3PresignedGetURL = s3AsyncService.generatePresignedGetURL(shorts.getShortsS3key(), Duration.ofMinutes(30));
        return ShortsDto.builder()
                .shortsId(shorts.getShortsId())
                .shortsTime(shorts.getShortsTime())
                .shortsS3Key(shorts.getShortsS3key())
                .shortsS3URL(s3PresignedGetURL)
                .build();
    }

    /**
     * 쇼츠 목록을 조회할 경우 시도한 사람 수를 포함하는 ShortsDto 리스트를 조회함
     * 각 객체에 서명된 Get URL을 더해서 최종 리스트를 반환함
     *
     * @return ShortsDto 리스트
     */
    @Override
    public List<ShortsDto> findShortsList() {
        return shortsRepository.findShortsList().stream()
                .map(this::withPresignedGetURL)
                .toList();
    }

    /**
     * 쇼츠 목록을 조회할 경우 시도한 사람 수를 포함하는 ShortsDto 리스트를 조회함
     * 시도한 사람 수가 많은 순서대로 3개를 조회한 후,
     * 각 객체에 서명된 Get URL을 더해서 최종 리스트를 반환함
     *
     * @return ShortsDto 객체 3개
     */
    @Override
    public List<ShortsDto> findPopularShortsList() {
        return shortsRepository.findPopularShortsList().stream()
                .map(this::withPresignedGetURL)
                .toList();

    }

    /**
     * 특정 회원의 시도한 쇼츠 개수, 녹화한 쇼츠 개수, 시도했지만 녹화하지 않은 쇼츠 제목을 ShortsStatsDto 객체로 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return ShortsStatsDto 객체
     */
    @Transactional
    @Override
    public ShortsStatsDto findShortsStats(String accessToken) {
        int memberIndex = memberService.getMemberIndex(accessToken);
        return shortsRepository.findShortsStatsDto(memberIndex);

    }

    /**
     * Shorts DB에서 객체를 조회함
     *
     * @param shortsId Shorts 객체의 id
     * @return Shorts 객체
     * @throws CustomException Shorts DB에 해당 객체가 없으면 예외 처리
     */
    @Override
    public Shorts findShorts(int shortsId) {
        return shortsRepository.findById(shortsId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_NOT_FOUND));
    }

    /**
     * 기존 ShortsDto 객체에 서명된 Get URL을 추가하여 반환합니다.
     *
     * @param shortsDto ShortsDto 객체
     * @return S3 URL이 추가된 ShortsDto 객체
     */
    @Override
    public ShortsDto withPresignedGetURL(ShortsDto shortsDto) {
        return shortsDto.withShortsS3URL(s3AsyncService.generatePresignedGetURL(shortsDto.getShortsS3key(), Duration.ofMinutes(30)));

    }


}

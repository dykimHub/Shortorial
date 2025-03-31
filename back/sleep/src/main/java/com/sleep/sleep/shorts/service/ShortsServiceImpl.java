package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.constants.S3key;
import com.sleep.sleep.s3.service.S3AsyncServiceImpl;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
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
    private final TriedShortsRepository triedShortsRepository;
    private final S3AsyncServiceImpl s3AsyncServiceImpl;

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
        String s3key = S3key.ORIGIN.buildS3key(shorts.getShortsTitle());
        String s3PresignedGetURL = s3AsyncServiceImpl.generatePresignedGetURL(s3key, Duration.ofMinutes(30));
        return ShortsDto.builder()
                .shortsId(shorts.getShortsId())
                .shortsTitle(shorts.getShortsTitle())
                .shortsS3Key(s3key)
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
     * 특정 회원의 시도한 쇼츠 개수, 녹화한 쇼츠 개수, 업로드한 쇼츠 개수를 ShortsStatsDto 객체로 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return ShortsStatsDto 객체
     * @throws CustomException 회원의 쇼츠 통계를 불러올 수 없음
     */
    @Transactional
    @Override
    public ShortsStatsDto findShortsStats(String accessToken) {
        String memberId = memberService.findMemberId(accessToken);
        return shortsRepository.findShortsStatsDto(memberId)
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
        String memberId = memberService.findMemberId(accessToken);
        return shortsRepository.findTriedShortsList(memberId);
//                map 메서드는 Optinal 비어있으면 변환 작업을 수행하지 않음
//                각 시간대별 변환을 프론트 라이브러리가 처리
//                .map(tlist -> tlist.stream()
//                        .map(t -> t.withTriedShortsDate(t.getTriedShortsDate().plusHours(9)))
//                        .toList())

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

        TriedShorts triedShorts = triedShortsRepository.findTriedShorts(member.getMemberId(), shorts.getShortsId())
                .map(ts -> { // 이미 존재한다면
                    ts.updateTriedShortsDate(); // 시도한 날짜를 업데이트
                    return ts;
                })
                .orElseGet( // 존재하지 않는다면
                        () -> TriedShorts.builder()
                                .member(member)
                                .shorts(shorts)
                                .build() // 새로운 TriedShorts 객체 생성
                );

        triedShortsRepository.save(triedShorts); // 시도한 쇼츠 DB에 저장
        return SuccessResponse.of("회원이 시도한 쇼츠에 추가되었습니다.");
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
        String memberId = memberService.findMemberId(accessToken);
        TriedShorts triedShorts = triedShortsRepository.findTriedShorts(memberId, shortsId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TRIED_SHORTS_NOT_FOUND));

        triedShortsRepository.delete(triedShorts);

        return SuccessResponse.of("회원이 시도한 쇼츠에서 삭제되었습니다.");
    }

    /**
     * Shorts DB에서 객체를 조회함
     *
     * @param shortsId Shorts 객체의 id
     * @return Shorts 객체
     * @throws CustomException Shorts DB에 해당 객체가 없으면 예외 처리
     */
    private Shorts findShorts(int shortsId) {
        return shortsRepository.findById(shortsId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_NOT_FOUND));
    }

    /**
     * 기존 ShortsDto 객체에 서명된 Get URL을 추가하여 반환합니다.
     *
     * @param shortsDto ShortsDto 객체
     * @return 새로운 ShortsDto 객체
     */
    private ShortsDto withPresignedGetURL(ShortsDto shortsDto) {
        String s3key = S3key.ORIGIN.buildS3key(shortsDto.getShortsTitle());
        String s3PresignedGetURL = s3AsyncServiceImpl.generatePresignedGetURL(s3key, Duration.ofMinutes(30));
        return shortsDto.withShortsS3URL(s3PresignedGetURL);

    }


}

package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TriedShortsServiceImpl implements TriedShortsService {
    private final MemberService memberService;
    private final ShortsRepository shortsRepository;
    private final TriedShortsRepository triedShortsRepository;
    private final ShortsService shortsService;

    /**
     * 특정 id의 회원의 TriedShorts 리스트를 조회하여 TriedShortsDto 리스트로 변환함
     * TriedShortsDto 객체에 포함된 ShortsDto 객체에 서명된 Get URL을 붙인 후 최종 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return triedShortsDto 리스트
     */
    @Override
    public List<TriedShortsDto> findTriedShortsList(String accessToken) {
        int memberIndex = memberService.getMemberIndex(accessToken);
        return shortsRepository.findTriedShortsList(memberIndex).stream()
                .map(this::withPresignedGetURL)
                .toList();
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
        Member member = memberService.findByMemberId(memberService.getMemberId(accessToken));
        Shorts shorts = shortsService.findShorts(shortsId);

        TriedShorts triedShorts = triedShortsRepository.findTriedShorts(member.getMemberIndex(), shorts.getShortsId())
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
        int memberIndex = memberService.getMemberIndex(accessToken);
        TriedShorts triedShorts = triedShortsRepository.findTriedShorts(memberIndex, shortsId)
                .orElseThrow(() -> new CustomException(ExceptionCode.TRIED_SHORTS_NOT_FOUND));

        triedShortsRepository.delete(triedShorts);
        return SuccessResponse.of("회원이 시도한 쇼츠에서 삭제되었습니다.");
    }

    /**
     * 기존 TriedShortsDto 객체의 ShortsDto 객체에 서명된 Get URL을 추가하여 반환합니다.
     *
     * @param triedShortsDto TriedShortsDto 객체
     * @return S3 URL이 추가된 ShortsDto 객체가 포함된 TriedShortsDto 객체
     */
    private TriedShortsDto withPresignedGetURL(TriedShortsDto triedShortsDto) {
        ShortsDto shortsDto = shortsService.withPresignedGetURL(triedShortsDto.getShortsDto());
        return triedShortsDto.withShortsDto(shortsDto);

    }
}

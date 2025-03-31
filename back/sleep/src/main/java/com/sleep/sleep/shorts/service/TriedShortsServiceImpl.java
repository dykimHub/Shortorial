package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.service.S3AsyncServiceImpl;
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
    private final S3AsyncServiceImpl s3AsyncServiceImpl;
    private final ShortsService shortsService;

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
        Shorts shorts = shortsService.findShorts(shortsId);

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
}

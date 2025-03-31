package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.service.S3AsyncServiceImpl;
import com.sleep.sleep.shorts.dto.ModifiedShortsDto;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RecordedShortsServiceImpl implements RecordedShortsService {
    private final MemberService memberService;
    private final TriedShortsRepository triedShortsRepository;
    private final S3AsyncServiceImpl s3AsyncServiceImpl;
    private final RecordedShortsRepository recordedShortsRepository;

    /**
     * 특정 id의 회원의 RecordedShortsDto 리스트를 반환함
     *
     * @param accessToken 로그인한 회원의 token
     * @return RecordedShortsDto 리스트
     */
    @Override
    public List<RecordedShortsDto> findRecordedShortsList(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);
        return null;
    }

    /**
     * 특정 id의 회원이 녹화한 쇼츠를 recorded_shorts에 등록함
     * 녹화된 쇼츠의 제목을 활용하여 S3 링크를 얻고 메타데이터에서 S3에 저장된 시간을 얻음
     *
     * @param accessToken         로그인한 회원의 token
     * @param recordedShortsS3Key 녹화하고 S3에 업로드된 쇼츠의 S3key
     * @return RecordedShorts 등록에 성공하면 SuccessResponse 객체를 반환함
     */
    @Transactional
    @Override
    public SuccessResponse addRecordedShorts(String accessToken, String recordedShortsS3Key) {
        Member member = memberService.findMemberEntity(accessToken);

        String recordedShortsTitle = recordedShortsS3Key.split("/")[1];
        String recordedShortsS3URL = null;

        RecordedShorts recordedShorts = RecordedShorts.builder()
                .recordedShortsTitle(recordedShortsTitle)
                .recordedShortsS3key(recordedShortsS3Key)
                .recordedShortsS3URL(recordedShortsS3URL)
                .member(member)
                .build();

        recordedShortsRepository.save(recordedShorts);

        return SuccessResponse.of("회원이 녹화한 쇼츠가 저장되었습니다.");
    }

    /**
     * 특정 id의 회원이 녹화한 쇼츠의 제목을 변경함
     *
     * @param accessToken       로그인한 회원의 token
     * @param modifiedShortsDto 제목을 수정할 RecordedShorts 객체의 id와 새로운 제목이 포함된 ModifiedShortsDto 객체
     * @return 제목 변경에 성공하면 SuccessResponse 객체를 반환함
     * @throws CustomException 특정 id의 회원이 녹화한 쇼츠 중에 겹치는 제목이 있음
     */
    @Transactional
    @Override
    public SuccessResponse modifyRecordedShortsTitle(String accessToken, ModifiedShortsDto modifiedShortsDto) {
        RecordedShorts recordedShorts = recordedShortsRepository.findById(modifiedShortsDto.getRecordedShortsId())
                .orElseThrow(() -> new CustomException(ExceptionCode.RECORDED_SHORTS_NOT_FOUND));
        String memberId = memberService.findMemberId(accessToken);

        recordedShortsRepository.findByRecordedShortsTitle(memberId, modifiedShortsDto.getNewRecordedShortsTitle())
                .ifPresent(r -> {
                    throw new CustomException(ExceptionCode.EXISTED_RECORDED_SHORTS_TITLE);
                });

        recordedShortsRepository.modifyRecordedShortsTitle(recordedShorts.getRecordedShortsId(), modifiedShortsDto.getNewRecordedShortsTitle());

        return SuccessResponse.of("회원이 녹화한 쇼츠가 새로운 제목으로 변경되었습니다.");
    }

    /**
     * RecordedShorts 객체의 삭제를 시도하면 is_deleted 열을 1로 변환함
     *
     * @param S3key 삭제할 RecordedShorts 객체의 S3 key
     * @return 삭제에 성공하면 SuccessResponse 객체를 반환함
     */
    @Transactional
    @Override
    public SuccessResponse deleteRecordedShorts(String S3key) {
        RecordedShorts recordedShorts = recordedShortsRepository.findByRecordedShortsByS3key(S3key)
                .orElseThrow(() -> new CustomException(ExceptionCode.RECORDED_SHORTS_NOT_FOUND));

        recordedShortsRepository.deleteById(recordedShorts.getRecordedShortsId());

        return SuccessResponse.of("회원이 녹화한 쇼츠가 삭제되었습니다.");
    }

}
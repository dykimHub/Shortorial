package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.constants.S3Status;
import com.sleep.sleep.s3.constants.S3key;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;
import com.sleep.sleep.s3.service.S3AsyncService;
import com.sleep.sleep.shorts.dto.ModifyingStatusDto;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecordedShortsServiceImpl implements RecordedShortsService {
    private final MemberService memberService;
    private final ShortsService shortsService;
    private final S3AsyncService s3AsyncService;
    private final RecordedShortsRepository recordedShortsRepository;
    private final ShortsRepository shortsRepository;

    /**
     * S3PutResponseDTO를 활용하여 RecordedShorts 객체를 생성하고 녹화한 쇼츠 DB에 저장합니다.
     * S3 Put용 서명된 URL을 생성한 후 S3 key와 함께 S3PutResponseDTO를 생성하여 반환합니다.
     *
     * @param accessToken     로그인한 회원의 토큰
     * @param s3PutRequestDTO S3에 업로드할 때 필요한 정보가 담긴 S3PutRequestDTO 객체
     * @return s3key와 서명된 Put URL이 담긴 S3PutResponseDTO 객체
     */
    @Transactional
    @Override
    public S3PutResponseDTO addRecordedShorts(String accessToken, S3PutRequestDTO s3PutRequestDTO) {
        Member member = memberService.findByMemberId(memberService.getMemberId(accessToken));
        Shorts shorts = shortsService.findShorts(s3PutRequestDTO.getShortsId());
        // UUID를 활용하여 파일 제목을 임의 지정
        String fileName = UUID.randomUUID().toString();
        // 파일 제목으로 s3 key 생성(S3 User 폴더에 업로드)
        String recordedShortsS3key = S3key.USERS.buildS3key(member.getMemberId(), fileName);
        String presignedPutURL = s3AsyncService.generatePresignedPutURL(recordedShortsS3key, s3PutRequestDTO.getMetadata(), Duration.ofMinutes(10));

        // 음악 처리 성공하면 S3 Lambda 폴더에 최종 업로드
        String processedShortsS3key = S3key.LAMBDA.buildS3key(member.getMemberId(), fileName);
        RecordedShorts newRecordedShorts = RecordedShorts.builder()
                .recordedShortsTitle(fileName)
                .recordedShortsS3key(processedShortsS3key)
                .status(S3Status.PENDING) // S3 업로드 되기 전이므로 PENDING 상태로 기록
                .shorts(shorts)
                .member(member)
                .build();

        recordedShortsRepository.save(newRecordedShorts);

        return S3PutResponseDTO.builder()
                .processedShortsS3key(processedShortsS3key)
                .presignedPutURL(presignedPutURL)
                .build();

    }

    /**
     * 녹화된 쇼츠의 상태를 업데이트 합니다.
     * AWS S3 PUT 성공 / 실패, AWS Lambda 처리 성공 / 실패했을 때 호출됩니다.
     *
     * @param modifyingStatusDto 녹화된 쇼츠의 S3 key와 업로드 상태가 담긴 ModifyingStatusDto 객체
     * @return status 컬럼을 업데이트했다면 SuccessResponse 객체 반환
     * @throws CustomException status 컬럼 업데이트에 실패했다면 예외 처리
     */
    @Transactional
    @Override
    public SuccessResponse modifyRecordedShortsStatus(ModifyingStatusDto modifyingStatusDto) {
        int result = recordedShortsRepository.modifyRecordedShortsStatus(modifyingStatusDto.getRecordedShortsS3key(), modifyingStatusDto.getStatus());
        if (result < 1) throw new CustomException(ExceptionCode.RECORDED_SHORTS_UPDATE_FAILED);
        log.info("Modifying Status: s3key={}, status={}", modifyingStatusDto.getRecordedShortsS3key(), modifyingStatusDto.getStatus());
        return SuccessResponse.of("녹화 파일 업로드 상태가 성공적으로 업데이트 되었습니다.");

    }

    /**
     * 해당 회원이 녹화한 쇼츠 목록을 조회합니다.
     * 녹화한 쇼츠의 S3 key를 활용하여 서명된 Get URL을 생성한 후 함께 반환합니다.
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return RecordedShortsDto 객체 리스트
     */
    @Override
    public List<RecordedShortsDto> findRecordedShortsDtoList(String accessToken) {
        int memberIndex = memberService.getMemberIndex(accessToken);
        return shortsRepository.findRecordedShortsDtoList(memberIndex).stream()
                .map(this::withPresignedGetURL)
                .toList();
    }

    /**
     * RecordedShorts 객체의 삭제를 시도하면 is_deleted 열을 1로 변환함
     *
     * @param recordedShortsId 삭제할 RecordedShorts 객체의 recordedShortsId
     * @return 삭제에 성공하면 SuccessResponse 객체를 반환함
     */
    @Transactional
    @Override
    public SuccessResponse deleteRecordedShorts(int recordedShortsId) {
        int result = recordedShortsRepository.deleteRecordedShortsById(recordedShortsId);
        if (result < 1)
            throw new CustomException(ExceptionCode.RECORDED_SHORTS_NOT_FOUND);
        return SuccessResponse.of("회원이 녹화한 쇼츠가 삭제되었습니다.");
    }

    /**
     * 기존 RecordedShortsDto 객체에 서명된 Get URL을 추가하여 반환합니다.
     *
     * @param recordedShortsDto RecordedShortsDto 객체
     * @return S3 URL이 추가된 RecordedShortsDto 객체
     */
    private RecordedShortsDto withPresignedGetURL(RecordedShortsDto recordedShortsDto) {
        return recordedShortsDto.withRecordedShortsS3URL(s3AsyncService.generatePresignedGetURL(recordedShortsDto.getRecordedShortsS3key(), Duration.ofMinutes(30)));

    }
}
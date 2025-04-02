package com.sleep.sleep.shorts.service;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.constants.S3Status;
import com.sleep.sleep.s3.constants.S3key;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;
import com.sleep.sleep.s3.service.S3AsyncService;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RecordedShortsServiceImpl implements RecordedShortsService {
    private final MemberService memberService;
    private final ShortsService shortsService;
    private final S3AsyncService s3AsyncService;
    private final RecordedShortsRepository recordedShortsRepository;

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
        Member member = memberService.findMemberEntity(accessToken);
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
                .status(S3Status.PENDING.getStatus()) // S3 업로드 되기 전이므로 PENDING 상태로 기록
                .shorts(shorts)
                .member(member)
                .build();

        recordedShortsRepository.save(newRecordedShorts);

        return S3PutResponseDTO.builder()
                .processedShortsS3key(processedShortsS3key)
                .presignedPutURL(presignedPutURL)
                .build();

    }

}
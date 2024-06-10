package com.sleep.sleep.shorts.service;

import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.s3.S3Service;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortsServiceImpl implements ShortsService {

    private final JwtTokenUtil jwtTokenUtil;
    private final S3Service s3Service;
    private final ShortsRepository shortsRepository;
    private final RecordedShortsRepository recordedShortsRepository;
//    private final MemberRepository memberRepository;
//    private final TriedShortsRepository triedShortsRepository;

    /**
     * token에서 memberId를 찾아서 반환함
     *
     * @param accessToken 해당 회원의 token
     * @return memberId
     */
    public String getMemberId(String accessToken) {
        return jwtTokenUtil.getUsername(accessToken.substring(7));
    }

    /**
     * shorts 객체를  ShostsDto 객체로 변환함
     *
     * @param shorts Shorts 객체
     * @param folderName s3 버킷 내 폴더 이름(shortsList)
     * @return ShortsDto로 변환된 객체
     */
    public ShortsDto convertToShortsDto(Shorts shorts, String folderName) {
        String S3key = folderName + shorts.getShortsTitle();

        ShortsDto shortsDto = ShortsDto.builder()
                .shortsId(shorts.getShortsId())
                .shortsTime(shorts.getShortsTime())
                .shortsTitle(shorts.getShortsTitle())
                .shortsMusicTitle(shorts.getShortsMusicTitle())
                .shortsMusicSinger(shorts.getShortsMusicSinger())
                .shortsS3Link(s3Service.getPath(S3key))
                .shortsSource(shorts.getShortsSource())
                .shortsChallengerNum(shorts.getShortsChallengerNum())
                .build();

        return shortsDto;

    }

    /**
     * recordedShorts 객체를 recordedShortsDto 객체로 반환
     * 
     * @param recordedShorts recordedShorts객체
     * @param folderName S3 버킷 내 폴더 이름(memberId)
     * @return recordedShortsDto로 변환된 객체
     */
    public RecordedShortsDto convertToRecordedShortsDto(RecordedShorts recordedShorts, String folderName) {
        String S3key = folderName + recordedShorts.getRecordedShortsTitle();

        RecordedShortsDto recordedShortsDto = RecordedShortsDto.builder()
                .recordedShortsId(recordedShorts.getRecordedShortsId())
                .recordedShortsTitle(recordedShorts.getRecordedShortsTitle())
                .recordedShortsS3Link(s3Service.getPath(S3key))
                .shortsMusicTitle(recordedShorts.getShorts().getShortsMusicTitle())
                .shortsMusicSigner(recordedShorts.getShorts().getShortsMusicSinger())
                .recordedShortsDate(recordedShorts.getRecordedShortsDate())
                .recordedShortsYoutubeUrl(recordedShorts.getRecordedShortsYoutubeUrl())
                .build();

        return recordedShortsDto;

    }

    /**
     * 특정 Shorts ID에 해당하는 Shorts 객체를 ShortsDto 객체로 반환
     *
     * @param shortsId Shorts ID
     * @return ShortsDto로 변환된 객체
     * @throws CustomException 해당 Shorts 객체를 찾을 수 없음
     */
    @Override
    public ShortsDto findShorts(int shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_NOT_FOUND));
        ShortsDto shortsDto = convertToShortsDto(shorts, "shortsList/");

        return shortsDto;
    }


    /**
     * 모든 Shorts 객체를 ShortsDto 리스트로 반환함
     *
     * @return 변환된 ShortsDto 리스트
     * @throws CustomException Shorts 리스트가 비어있음
     */
    @Override
    public List<ShortsDto> findShortsList() {
        List<Shorts> shortsList = shortsRepository.findAll();
        if (shortsList.isEmpty()) throw new CustomException(ExceptionCode.ALL_SHORTS_NOT_FOUND);

        List<ShortsDto> shortsDtoList = shortsList.stream()
                .map(s -> convertToShortsDto(s, "shortsList/"))
                .toList();

        return shortsDtoList;
    }

    /**
     * shortsDto 객체를 클릭한 수가 많은 순서대로 3개 반환함
     *
     * @return ShortsDto로 변환된 객체 3개
     * @throws CustomException 인기 Shorts 리스트가 비어있음
     */
    @Override
    public List<ShortsDto> findPopularShortsList() {
        List<Shorts> popularShortsList = shortsRepository.findPopularShorts();
        if (popularShortsList.isEmpty()) throw new CustomException(ExceptionCode.POPULAR_SHORTS_NOT_FOUND);

        List<ShortsDto> popularShortsDtoList = popularShortsList.stream()
                .map(s -> convertToShortsDto(s, "shortsList/"))
                .toList();

        return popularShortsDtoList;

    }

    /**
     * 특정 memberId에 해당하는 recordedShortsDto 리스트를 반환함
     *
     * @param accessToken 회원의 token
     * @return 변환된 recordedShortsDto 리스트
     */
    @Override
    public List<RecordedShortsDto> findRecordedShortsList(String accessToken) {
        String memberId = getMemberId(accessToken);

        List<RecordedShorts> recordedShortsList = recordedShortsRepository.findByMemberId(memberId);
        if(recordedShortsList.isEmpty()) return new ArrayList<>();

        List<RecordedShortsDto> recordedShortsDtoList = recordedShortsList.stream()
                .map(r -> convertToRecordedShortsDto(r, memberId + "/"))
                .toList();

        return recordedShortsDtoList;
    }

//    @Override
//    public List<RecordedShortsDto> findRecordedShortsList(String memberId) {
//        int memberNo = memberRepository.findByMemberId(username)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found")).getMemberIndex();
//
//        List<RecordedShorts> shorts = recordedShortsRepository.findUploadShortList(memberNo);
//
//        List<RecordedShortsDto> uploadShorts = new ArrayList<>();
//
//        for (RecordedShorts value : shorts) {
//            RecordedShortsDto RecordedShortsDto = new RecordedShortsDto();
//            RecordedShortsDto.setUploadNo(value.getUploadNo());
//            RecordedShortsDto.setMemberNo(memberNo);
//            RecordedShortsDto.setUploadUrl(value.getUploadUrl());
//            RecordedShortsDto.setUploadTitle(value.getUploadTitle());
//            RecordedShortsDto.setUploadDate(value.getUploadDate().toString());
//            RecordedShortsDto.setYoutubeUrl(value.getYoutubeUrl());
//
//            uploadShorts.add(RecordedShortsDto);
//        }
//        return uploadShorts;
//    }

//    public void upload(RecordedShortsDto dto, String username) {
//        Member member = memberRepository.findByMemberId(username)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
//        recordedShortsRepository.save(RecordedShorts.builder()
//                .memberIndex(member)
//                .uploadUrl(dto.getUploadUrl())
//                .uploadTitle(dto.getUploadTitle())
//                .build());
//    }
//
//    public void putTitle(int uploadNo, String oldTitle, String newTitle, String newURL) {
//        RecordedShorts recordedShorts = recordedShortsRepository.findByUploadTitle(uploadNo, oldTitle);
//
//        if (recordedShorts != null) {
//
//            recordedShorts.update(newTitle, newURL);
//
//            recordedShortsRepository.save(recordedShorts);
//        }
//
//    }
//
//    public boolean isNameExists(String title) {
//        return recordedShortsRepository.existsByUploadTitle(title);
//    }
//
//    public void deleteUploadShorts(int uploadNo, String fileName) {
//        RecordedShorts recordedShorts = recordedShortsRepository.findByUploadTitle(uploadNo, fileName);
//        if (recordedShorts != null) {
//            recordedShortsRepository.delete(recordedShorts);
//        }
//    }
//
//    @Transactional
//    public void addTryCount(String username, int shortsNo) {
//        // 멤버 찾기
//        Member member = memberRepository.findByMemberId(username)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
//        log.info("member " + member.getMemberIndex());
//
//        // 쇼츠 찾기
//        Shorts shorts = shortsRepository.findById(shortsNo)
//                .orElseThrow(() -> new IllegalArgumentException("Shorts not found"));
//        log.info("shorts " + shorts.getShortsNo());
//
//        // 해당 멤버와 쇼츠에 대한 시도 찾기
//        Optional<TryShorts> tryShortsOpt = triedShortsRepository.findByMemberIndexAndShortsNo(member.getMemberIndex(), shorts.getShortsNo());
//
//        if (!tryShortsOpt.isPresent()) {
//            // 시도가 없으면 새로 생성
//            shorts.addShortsChallengers(1);
//
//            // TryShorts 엔티티 생성 및 저장
//            TryShorts tryShorts = TryShorts.builder()
//                    .memberIndex(member)
//                    .shortsNo(shorts)
//                    .build();
//            triedShortsRepository.save(tryShorts);
//            log.info("New tryShorts created for shortsNo: " + tryShorts.getShortsNo());
//        } else {
//            TryShorts tryShorts = tryShortsOpt.get();
//            // 시도가 이미 있으면 uploadDate를 현재 시간으로 수정
//            tryShorts.setUploadDate(LocalDateTime.now());
//            triedShortsRepository.save(tryShorts);
//            log.info("UploadDate updated for tryShorts with shortsNo: " + tryShorts.getShortsNo());
//        }
//    }
//
//    public List<ShortsDto> getTryShortsList(String username) {
//        // 시도한 영상 리스트 가져오기
//
//        // 회원 번호 조회
//        int memberNo = memberRepository.findByMemberId(username)
//                .orElseThrow(() -> new IllegalArgumentException("Member not found")).getMemberIndex();
//
//        // 시도한 영상 리스트 조회
//        List<TryShorts> tryShortsList = triedShortsRepository.findTryShortList(memberNo);
//
//        // 시도한 영상 리스트를 담을 DTO 리스트
//        List<ShortsDto> shortsDtoList = new ArrayList<>();
//
//        // 시도한 영상 리스트를 순회하면서 DTO에 데이터 추가
//        for (TryShorts tryShorts : tryShortsList) {
//            ShortsDto shortsDto = new ShortsDto();
//
//            // 시도한 영상의 ShortsNo를 사용하여 Shorts 엔티티 조회
//            Shorts shorts = tryShorts.getShortsNo();
//
//            // Shorts 엔티티의 데이터를 DTO에 추가
//            shortsDto.setShortsNo(shorts.getShortsNo());
//            shortsDto.setShortsUrl(shorts.getShortsUrl());
//            shortsDto.setShortsTime(shorts.getShortsTime());
//            shortsDto.setShortsTitle(shorts.getShortsTitle());
//            shortsDto.setShortsDirector(shorts.getShortsDirector());
//            shortsDto.setShortsChallengers(shorts.getShortsChallengers());
//            shortsDto.setShortsLink(shorts.getShortsLink());
//            shortsDto.setShortDate(shorts.getShortsDate());
//
//            // DTO 리스트에 DTO 추가
//            shortsDtoList.add(shortsDto);
//        }
//
//        return shortsDtoList;
//    }
//
//    public void putYoutubeUrl(int uploadNo, String url) {
//        RecordedShorts recordedShorts = recordedShortsRepository.findByUploadNo(uploadNo);
//
//        if (recordedShorts != null) {
//
//            recordedShorts.putYoutubeUrl(url);
//
//            recordedShortsRepository.save(recordedShorts);
//        }
//    }

}

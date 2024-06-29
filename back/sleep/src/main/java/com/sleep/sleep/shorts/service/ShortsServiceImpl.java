package com.sleep.sleep.shorts.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.service.MemberService;
import com.sleep.sleep.s3.S3Service;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import com.sleep.sleep.shorts.repository.RecordedShortsRepository;
import com.sleep.sleep.shorts.repository.ShortsRepository;
import com.sleep.sleep.shorts.repository.TriedShortsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShortsServiceImpl implements ShortsService {

    private final JwtTokenUtil jwtTokenUtil;
    private final S3Service s3Service;
    private final MemberService memberService;
    private final ShortsRepository shortsRepository;
    private final TriedShortsRepository triedShortsRepository;
    private final RecordedShortsRepository recordedShortsRepository;

    /**
     * 특정 id의 Shorts 객체를 반환
     *
     * @param shortsId Shorts 객체의 id
     * @return Shorts 객체
     * @throws CustomException 해당 Shorts 객체를 찾을 수 없음
     */
    public Shorts findShortsEntity(int shortsId) {
        Shorts shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new CustomException(ExceptionCode.SHORTS_NOT_FOUND));

        return shorts;
    }

    /**
     * Shorts 객체를  ShostsDto 객체로 변환함
     *
     * @param shorts Shorts 객체
     * @return ShortsDto 형으로 변환된 객체
     */
    public ShortsDto convertToShortsDto(Shorts shorts) {
        ShortsDto shortsDto = ShortsDto.builder()
                .shortsId(shorts.getShortsId())
                .shortsTime(shorts.getShortsTime())
                .shortsTitle(shorts.getShortsTitle())
                .shortsMusicTitle(shorts.getShortsMusicTitle())
                .shortsMusicSinger(shorts.getShortsMusicSinger())
                .shortsS3Link(s3Service.getPath("shortsList", shorts.getShortsTitle()))
                .shortsSource(shorts.getShortsSource())
                .shortsChallengerNum(shorts.getTriedShortsList().size())
                .build();

        return shortsDto;

    }

    /**
     * RecordedShorts 객체를 RecordedShortsDto 객체로 반환
     *
     * @param recordedShorts RecordedShorts 객체
     * @return RecordedShortsDto 형으로 변환된 객체
     */
    public RecordedShortsDto convertToRecordedShortsDto(RecordedShorts recordedShorts) {
        RecordedShortsDto recordedShortsDto = RecordedShortsDto.builder()
                .recordedShortsId(recordedShorts.getRecordedShortsId())
                .recordedShortsTitle(recordedShorts.getRecordedShortsTitle())
                .recordedShortsS3Link(recordedShorts.getRecordedShortsS3Link())
                .recordedShortsDate(recordedShorts.getRecordedShortsDate())
                .recordedShortsYoutubeUrl(recordedShorts.getRecordedShortsYoutubeUrl())
                .build();

        return recordedShortsDto;

    }

    /**
     * TriedShorts 객체를 TriedShortsDto 객체로 반환
     *
     * @param triedShorts TriedShorts 객체
     * @return TriedShortsDto 형으로 변환된 객체
     */
    public TriedShortsDto convertToTriedShortsDto(TriedShorts triedShorts) {
        TriedShortsDto triedShortsDto = TriedShortsDto.builder()
                .triedShortsId(triedShorts.getTryShortsId())
                .triedShortsDate(triedShorts.getTriedShortsDate())
                .shortsDto(convertToShortsDto(triedShorts.getShorts()))
                .build();

        return triedShortsDto;

    }


    /**
     * 특정 id의 Shorts 객체를 ShortsDto 객체로 반환
     *
     * @param shortsId Shorts 객체의 id
     * @return ShortsDto 객체
     */
    @Override
    public ShortsDto findShorts(int shortsId) {
        Shorts shorts = findShortsEntity(shortsId);
        ShortsDto shortsDto = convertToShortsDto(shorts);

        return shortsDto;
    }


    /**
     * Shorts DB의 모든 Shorts 객체를 ShortsDto 리스트로 반환함
     *
     * @return ShortsDto 리스트
     * @throws CustomException Shorts DB를 조회할 수 없음
     */
    @Override
    public List<ShortsDto> findShortsList() {
        List<Shorts> shortsList = shortsRepository.findShortsList();
        if (shortsList.isEmpty()) throw new CustomException(ExceptionCode.ALL_SHORTS_NOT_FOUND);

        List<ShortsDto> shortsDtoList = shortsList.stream()
                .map(this::convertToShortsDto)
                .toList();

        return shortsDtoList;
    }

    /**
     * Shorts 객체의 triedShortsList가 많은 순서대로 ShortDto형으로 바꿔서 3개를 반환함
     *
     * @return ShortsDto 객체 3개
     * @throws CustomException 인기 쇼츠 리스트를 불러올 수 없음
     */
    @Override
    public List<ShortsDto> findPopularShortsList() {
        List<Shorts> popularShortsList = shortsRepository.findPopularShorts();
        if (popularShortsList.isEmpty()) throw new CustomException(ExceptionCode.POPULAR_SHORTS_NOT_FOUND);

        List<ShortsDto> popularShortsDtoList = popularShortsList.stream()
                .map(this::convertToShortsDto)
                .toList();

        return popularShortsDtoList;

    }

    /**
     * 특정 id의 회원의 TriedShorts 리스트를 TriedShortsDto 리스트로 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return triedShortsDto 리스트
     */
    @Override
    public List<TriedShortsDto> findTriedShortsList(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);

        List<TriedShorts> triedShortsList = shortsRepository.findTriedShortsList(member);
        //if (triedShortsList.isEmpty()) return new ArrayList<>();

        List<TriedShortsDto> triedShortsDtoList = triedShortsList.stream()
                .map(this::convertToTriedShortsDto)
                .toList();

        return triedShortsDtoList;
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
        Shorts shorts = findShortsEntity(shortsId);

        TriedShorts triedShorts = shortsRepository.findTriedShorts(member, shorts);

        if (triedShorts != null) {
            triedShorts.updateTriedShortsDate(LocalDateTime.now());
            triedShortsRepository.save(triedShorts);
            return SuccessResponse.of("이미 시도한 쇼츠입니다. 시도한 날짜를 변경합니다.");

        }

        TriedShorts newTriedShorts = TriedShorts.builder()
                .member(member)
                .shorts(shorts)
                .build();

        triedShortsRepository.save(newTriedShorts);

        return SuccessResponse.of("회원이 시도한 쇼츠에 추가되었습니다.");
    }

    /**
     * 특정 id의 TriedShorts 객체를 삭제함
     *
     * @param accessToken   로그인한 회원의 토큰
     * @param triedShortsId TriedShorts 객체의 id
     * @return TriedShorts 삭제에 성공하면 SuccessResponse 객체를 반환함
     * @throws CustomException 해당 TriedShorts 객체를 찾을 수 없음
     */
    @Transactional
    @Override
    public SuccessResponse deleteTriedShorts(String accessToken, int triedShortsId) {

        TriedShorts triedShorts = triedShortsRepository.findById(triedShortsId).orElseThrow(() -> new CustomException(ExceptionCode.TRIED_SHORTS_NOT_FOUND));

        triedShortsRepository.delete(triedShorts);

        return SuccessResponse.of("회원이 시도한 쇼츠에서 삭제되었습니다.");
    }

    /**
     * 특정 id의 회원의 RecordedShortsDto 리스트를 반환함
     *
     * @param accessToken 로그인한 회원의 token
     * @return RecordedShortsDto 리스트
     */
    @Override
    public List<RecordedShortsDto> findRecordedShortsList(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);

        List<RecordedShorts> recordedShortsList = recordedShortsRepository.findByRecordedShortsList(member);
        //if (recordedShortsList.isEmpty()) return new ArrayList<>();

        List<RecordedShortsDto> recordedShortsDtoList = recordedShortsList.stream()
                .map(this::convertToRecordedShortsDto)
                .toList();

        return recordedShortsDtoList;
    }

    /**
     * 특정 id의 회원이 녹화한 쇼츠를 recorded_shorts에 등록함
     * 녹화된 쇼츠의 제목을 활용하여 S3 링크를 얻고 메타데이터에서 S3에 저장된 시간을 얻음
     *
     * @param accessToken 로그인한 회원의 token
     * @param recordedShortsTitle 녹화된 쇼츠의 제목
     * @return RecordedShorts 등록에 성공하면 SuccessResponse 객체를 반환함
     */
    @Transactional
    @Override
    public SuccessResponse addRecordedShorts(String accessToken, String recordedShortsTitle) {
        Member member = memberService.findMemberEntity(accessToken);

        String recordedShortsS3Link = s3Service.getPath(member.getMemberId(), recordedShortsTitle);
        ObjectMetadata objectMetaData = s3Service.getObjectMetaData(member.getMemberId(), recordedShortsTitle);

        RecordedShorts recordedShorts = RecordedShorts.builder()
                .recordedShortsTitle(recordedShortsTitle)
                .recordedShortsS3Link(recordedShortsS3Link)
                .recordedShortsDate(objectMetaData.getLastModified())
                .member(member)
                .build();

        recordedShortsRepository.save(recordedShorts);

        return SuccessResponse.of("녹화된 쇼츠가 저장되었습니다.");
    }

    /**
     * 특정 회원의 시도한 쇼츠 개수, 녹화한 쇼츠 개수, 업로드한 쇼츠 개수를 ShortsStatsDto 객체로 반환함
     *
     * @param accessToken 로그인한 회원의 토큰
     * @return ShortsStatsDto 객체
     */
    @Transactional
    @Override
    public ShortsStatsDto findShortsStats(String accessToken) {
        Member member = memberService.findMemberEntity(accessToken);
        ShortsStatsDto shortsStatsDto = shortsRepository.findShortsStats(member);

        if(shortsStatsDto == null) throw new CustomException(ExceptionCode.SHORTS_STATS_NOT_FOUND);

        return shortsStatsDto;
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

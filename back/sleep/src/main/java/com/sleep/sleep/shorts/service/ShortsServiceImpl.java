package com.sleep.sleep.shorts.service;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.repository.MemberRepository;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.UploadShortsDto;
import com.sleep.sleep.shorts.entity.*;
import com.sleep.sleep.shorts.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = false)
@Slf4j
public class ShortsServiceImpl implements ShortsService{

    private final ShortsRepository shortsRepository;
    private final UploadShortsRepository uploadShortsRepository;
    private final MemberRepository memberRepository;
    private final TryShortsRepository tryShortsRepository;

    //특정 쇼츠 조회
    public ShortsDto getShortsInfo(int shortsNo) {
        Shorts shorts = shortsRepository.findByShortsNo(shortsNo);

        ShortsDto shortsInfo = new ShortsDto();

        shortsInfo.setShortsNo(shorts.getShortsNo());
        shortsInfo.setShortsUrl(shorts.getShortsUrl());
        shortsInfo.setShortsTitle(shorts.getShortsTitle());
        shortsInfo.setShortsDirector(shorts.getShortsDirector());
        shortsInfo.setShortsChallengers(shorts.getShortsChallengers());
        shortsInfo.setShortsTime(shorts.getShortsTime());
        shortsInfo.setShortsLink(shorts.getShortsLink());
        shortsInfo.setShortDate(shorts.getShortsDate());

        return shortsInfo;
    }


    //리스트 조회
    public List<ShortsDto> getShortList() {
        List<Shorts> shorts = shortsRepository.findShortList();

        List<ShortsDto> shortsList = new ArrayList<>();

        for(Shorts value : shorts){
            ShortsDto shortsDto = new ShortsDto();

            shortsDto.setShortsNo(value.getShortsNo());
            shortsDto.setShortsUrl(value.getShortsUrl());
            shortsDto.setShortsTime(value.getShortsTime());
            shortsDto.setShortsTitle(value.getShortsTitle());
            shortsDto.setShortsDirector(value.getShortsDirector());
            shortsDto.setShortsChallengers(value.getShortsChallengers());
            shortsDto.setShortsLink(value.getShortsLink());
            shortsDto.setShortDate(value.getShortsDate());

            shortsList.add(shortsDto);
        }
        return shortsList;
    }

    @Override
    public List<ShortsDto> getShortRankingList() {
        List<Shorts> shorts = shortsRepository.findShortTopRanking();

        List<ShortsDto> shortsList = new ArrayList<>();

        for(Shorts value : shorts){
            ShortsDto shortsDto = new ShortsDto();

            shortsDto.setShortsNo(value.getShortsNo());
            shortsDto.setShortsUrl(value.getShortsUrl());
            shortsDto.setShortsTime(value.getShortsTime());
            shortsDto.setShortsTitle(value.getShortsTitle());
            shortsDto.setShortsDirector(value.getShortsDirector());
            shortsDto.setShortsChallengers(value.getShortsChallengers());
            shortsDto.setShortsLink(value.getShortsLink());
            shortsDto.setShortDate(value.getShortsDate());

        }

        return shortsList;

    }

    public List<UploadShortsDto> getUploadShortsList(String username){
        int memberNo = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found")).getMemberIndex();

        List<RecordedShorts> shorts = uploadShortsRepository.findUploadShortList(memberNo);

        List<UploadShortsDto> uploadShorts = new ArrayList<>();

        for(RecordedShorts value : shorts){
            UploadShortsDto UploadShortsDto = new UploadShortsDto();
            UploadShortsDto.setUploadNo(value.getUploadNo());
            UploadShortsDto.setMemberNo(memberNo);
            UploadShortsDto.setUploadUrl(value.getUploadUrl());
            UploadShortsDto.setUploadTitle(value.getUploadTitle());
            UploadShortsDto.setUploadDate(value.getUploadDate().toString());
            UploadShortsDto.setYoutubeUrl(value.getYoutubeUrl());

            uploadShorts.add(UploadShortsDto);
        }
        return uploadShorts;
    }

    public void upload(UploadShortsDto dto, String username) {
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        uploadShortsRepository.save(RecordedShorts.builder()
                .memberIndex(member)
                .uploadUrl(dto.getUploadUrl())
                .uploadTitle(dto.getUploadTitle())
                .build());;
    }

    public void putTitle(int uploadNo, String oldTitle, String newTitle, String newURL){
        RecordedShorts recordedShorts = uploadShortsRepository.findByUploadTitle(uploadNo, oldTitle);

        if (recordedShorts != null) {

            recordedShorts.update(newTitle, newURL);

            uploadShortsRepository.save(recordedShorts);
        }

    }

    public boolean isNameExists(String title){
        return uploadShortsRepository.existsByUploadTitle(title);
    }

    public void deleteUploadShorts(int uploadNo, String fileName){
        RecordedShorts recordedShorts = uploadShortsRepository.findByUploadTitle(uploadNo, fileName);
        if (recordedShorts != null) {
            uploadShortsRepository.delete(recordedShorts);
        }
    }

    @Transactional
    public void addTryCount(String username, int shortsNo) {
        // 멤버 찾기
        Member member = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        log.info("member " + member.getMemberIndex());

        // 쇼츠 찾기
        Shorts shorts = shortsRepository.findById(shortsNo)
                .orElseThrow(() -> new IllegalArgumentException("Shorts not found"));
        log.info("shorts " + shorts.getShortsNo());

        // 해당 멤버와 쇼츠에 대한 시도 찾기
        Optional<TryShorts> tryShortsOpt = tryShortsRepository.findByMemberIndexAndShortsNo(member.getMemberIndex(), shorts.getShortsNo());

        if (!tryShortsOpt.isPresent()) {
            // 시도가 없으면 새로 생성
            shorts.addShortsChallengers(1);

            // TryShorts 엔티티 생성 및 저장
            TryShorts tryShorts = TryShorts.builder()
                    .memberIndex(member)
                    .shortsNo(shorts)
                    .build();
            tryShortsRepository.save(tryShorts);
            log.info("New tryShorts created for shortsNo: " + tryShorts.getShortsNo());
        } else {
            TryShorts tryShorts = tryShortsOpt.get();
            // 시도가 이미 있으면 uploadDate를 현재 시간으로 수정
            tryShorts.setUploadDate(LocalDateTime.now());
            tryShortsRepository.save(tryShorts);
            log.info("UploadDate updated for tryShorts with shortsNo: " + tryShorts.getShortsNo());
        }
    }

    public List<ShortsDto> getTryShortsList(String username){
        // 시도한 영상 리스트 가져오기

        // 회원 번호 조회
        int memberNo = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found")).getMemberIndex();

        // 시도한 영상 리스트 조회
        List<TryShorts> tryShortsList = tryShortsRepository.findTryShortList(memberNo);

        // 시도한 영상 리스트를 담을 DTO 리스트
        List<ShortsDto> shortsDtoList = new ArrayList<>();

        // 시도한 영상 리스트를 순회하면서 DTO에 데이터 추가
        for(TryShorts tryShorts : tryShortsList){
            ShortsDto shortsDto = new ShortsDto();

            // 시도한 영상의 ShortsNo를 사용하여 Shorts 엔티티 조회
            Shorts shorts = tryShorts.getShortsNo();

            // Shorts 엔티티의 데이터를 DTO에 추가
            shortsDto.setShortsNo(shorts.getShortsNo());
            shortsDto.setShortsUrl(shorts.getShortsUrl());
            shortsDto.setShortsTime(shorts.getShortsTime());
            shortsDto.setShortsTitle(shorts.getShortsTitle());
            shortsDto.setShortsDirector(shorts.getShortsDirector());
            shortsDto.setShortsChallengers(shorts.getShortsChallengers());
            shortsDto.setShortsLink(shorts.getShortsLink());
            shortsDto.setShortDate(shorts.getShortsDate());

            // DTO 리스트에 DTO 추가
            shortsDtoList.add(shortsDto);
        }

        return shortsDtoList;
    }

    public void putYoutubeUrl(int uploadNo,String url){
        RecordedShorts recordedShorts = uploadShortsRepository.findByUploadNo(uploadNo);

        if (recordedShorts != null) {

            recordedShorts.putYoutubeUrl(url);

            uploadShortsRepository.save(recordedShorts);
        }
    }

}

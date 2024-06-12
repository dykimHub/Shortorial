package com.sleep.sleep.shorts.service;

import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShortsService {

    ShortsDto findShorts(int shortsId);

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    List<RecordedShortsDto> findRecordedShortsList(String accessToken);

    List<TriedShortsDto> findTriedShortsList(String accessToken);

//    List<RecordedShortsDto> findRecordedShortsList(String memberId);

    //사용자가 업로드한 영상 DB에 넣기
//    public void upload(RecordedShortsDto dto, String username);

//    //사용자가 업로드한 영상 이름 변경
//    public void putTitle(int uploadNo,String oldTitle, String newTitle, String newURL);
//
//    //사용자가 업로드한 영상 이름 중복 검사
//    public boolean isNameExists(String title);
//
//    //사용자가 업로드한 영상 db삭제
//    public void deleteUploadShorts(int uploadNo, String fileName);
//
//    //사용자가 시도한 영상 카운트
//    public void addTryCount(String username, int shortsNo);
//
//    //사용자가 시도한 영상 리스트
//    public List<ShortsDto> getTryShortsList(String username);
//
//    //유튜브 url 저장
//    public void putYoutubeUrl(int uploadNo,String url);
}

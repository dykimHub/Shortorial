package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.Shorts;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShortsService {

    ShortsDto findShortsDto(int shortsId);

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    ShortsStatsDto findShortsStats(String accessToken);

    List<TriedShortsDto> findTriedShortsList(String accessToken);

    SuccessResponse addTriedShorts(String accessToken, int shortsId);

    SuccessResponse deleteTriedShorts(String accessToken, int shortsId);

    List<RecordedShortsDto> findRecordedShortsList(String accessToken);

    SuccessResponse addRecordedShorts(String accessToken, String recordedShortsS3Key);

    SuccessResponse modifyRecordedShortsTitle(String accessToken, ModifiedShortsDto modifiedShortsDto);

    SuccessResponse deleteRecordedShorts(int recordedShortsId);

    Shorts findShorts(int shortsId);

    RecordedShorts findRecordedShorts(int recordedShortsId);

    ShortsDto convertToShortsDto(Shorts shorts);

    RecordedShortsDto convertToRecordedShortsDto(RecordedShorts recordedShorts);

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

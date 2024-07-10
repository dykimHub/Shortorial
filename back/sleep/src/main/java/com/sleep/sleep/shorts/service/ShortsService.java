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


}

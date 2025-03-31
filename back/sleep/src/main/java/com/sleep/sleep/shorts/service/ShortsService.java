package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;

import java.util.List;


public interface ShortsService {

    ShortsDto findShortsDto(int shortsId);

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    ShortsStatsDto findShortsStats(String accessToken);

    List<TriedShortsDto> findTriedShortsList(String accessToken);

    SuccessResponse addTriedShorts(String accessToken, int shortsId);

    SuccessResponse deleteTriedShorts(String accessToken, int shortsId);

}

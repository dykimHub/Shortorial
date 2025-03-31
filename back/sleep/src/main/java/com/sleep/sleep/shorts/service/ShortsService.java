package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.Shorts;

import java.util.List;


public interface ShortsService {

    ShortsDto findShortsDto(int shortsId);

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    ShortsStatsDto findShortsStats(String accessToken);

    Shorts findShorts(int shortsId);
}

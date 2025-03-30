package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShortsService {

    List<ShortsDto> findShortsList();

    ShortsDto findShortsDto(int shortsId);

    List<ShortsDto> findPopularShortsList();

    ShortsStatsDto findShortsStats(String accessToken);

    List<TriedShortsDto> findTriedShortsList(String accessToken);

    SuccessResponse addTriedShorts(String accessToken, int shortsId);

    SuccessResponse deleteTriedShorts(String accessToken, int shortsId);

}

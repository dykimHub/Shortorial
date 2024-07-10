package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortsRepositoryCustom {

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    List<TriedShortsDto> findTriedShortsList(int memberIndex);

    ShortsStatsDto findShortsStatsDto(int memberIndex);
}

package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortsRepositoryCustom {

    Optional<List<ShortsDto>> findShortsList();

    Optional<List<ShortsDto>> findPopularShortsList();

    Optional<List<TriedShortsDto>> findTriedShortsList(int memberIndex);

    Optional<ShortsStatsDto> findShortsStatsDto(int memberIndex);
}

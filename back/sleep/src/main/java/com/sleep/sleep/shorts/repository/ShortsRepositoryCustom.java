package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.ShortsStatsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortsRepositoryCustom {

    ShortsDto findShorts(Shorts shorts);

    List<ShortsDto> findShortsList();

    List<ShortsDto> findPopularShortsList();

    List<TriedShortsDto> findTriedShortsList(Member member);

    ShortsStatsDto findShortsStatsDto(Member member);


}

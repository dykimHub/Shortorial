package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.shorts.entity.Shorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortsRepositoryCustom {

    List<Shorts> findPopularShorts();

    TriedShorts findShortsAlreadyTried(Member member, Shorts shorts);

}

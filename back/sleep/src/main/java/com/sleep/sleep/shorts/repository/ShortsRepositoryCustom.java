package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.Shorts;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShortsRepositoryCustom {

    List<Shorts> findPopularShorts();
}

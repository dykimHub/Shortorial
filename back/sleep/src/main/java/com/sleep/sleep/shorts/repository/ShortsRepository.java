package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortsRepository extends JpaRepository<Shorts, Integer>, ShortsRepositoryCustom {


}
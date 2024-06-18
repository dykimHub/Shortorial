package com.sleep.sleep.shorts.repository;

import com.sleep.sleep.shorts.entity.Shorts;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShortsRepository extends JpaRepository<Shorts,Integer>, ShortsRepositoryCustom {

    @Query("SELECT s FROM Shorts s LEFT JOIN FETCH s.triedShortsList")
    List<Shorts> findShortsList();

}
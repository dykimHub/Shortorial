package com.sleep.sleep.shorts.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "shorts")
public class Shorts {

    // 쇼츠 테이블 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shortsId;

    // 쇼츠 시간
    @Column(nullable = false)
    private int shortsTime;

    // 쇼츠 제목
    @Column(nullable = false)
    private String shortsTitle;

    // 쇼츠 음악 제목
    @Column(nullable = false)
    private String shortsMusicTitle;

    // 쇼츠 가수
    @Column(nullable = false)
    private String shortsMusicSinger;

    // 쇼츠 출처
    @Column(nullable = false)
    private String shortsSource;

    @Column(name = "shorts_s3key", nullable = false, unique = true)
    private String shortsS3Key;

    // 쇼츠 s3 링크
    @Column(name = "shorts_s3url", length = 1024, nullable = false)
    private String shortsS3URL;

    // 시도한 쇼츠
    @OneToMany(mappedBy = "shorts")
    private List<TriedShorts> triedShortsList;

}
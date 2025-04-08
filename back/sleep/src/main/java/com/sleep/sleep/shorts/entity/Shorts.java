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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shortsId; // 쇼츠 테이블 id
    @Column(nullable = false)
    private int shortsTime; // 쇼츠 시간
    @Column(nullable = false)
    private String shortsTitle; // 쇼츠 제목
    @Column(nullable = false)
    private String shortsMusicTitle; // 쇼츠 음악 제목
    @Column(nullable = false)
    private String shortsMusicSinger; // 쇼츠 가수
    @Column(nullable = false)
    private String shortsSource; // 쇼츠 출처
    @Column(name = "shorts_s3key", nullable = false)
    private String shortsS3key; // 쇼츠 S3 key
    @OneToMany(mappedBy = "shorts")
    private List<TriedShorts> triedShortsList;
    @OneToMany(mappedBy = "shorts")
    private List<RecordedShorts> recordedShortsList;

}
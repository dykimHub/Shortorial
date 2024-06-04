package com.sleep.sleep.shorts.entity;

import jakarta.persistence.*;
import lombok.*;

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

    // 쇼츠 출처
    private String shortsSource;

    // 쇼츠 s3 링크
    private String shortsLink;

    // 시도한 쇼츠
    @OneToMany(mappedBy = "shortsNo", cascade = CascadeType.ALL)
    private List<TryShorts> tryShortsList;

}
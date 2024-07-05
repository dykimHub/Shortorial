package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recorded_shorts"//        ,indexes = {
//                @Index(name = "idx_member_recorded_shorts", columnList = "member_id, tried_shorts_id")
//        })
)
@Entity
public class RecordedShorts {

    // 녹화한 쇼츠 테이블 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordedShortsId;

    // 녹화한 쇼츠 제목
    // unique로 논클러스터링 INDEX 추가
    @Column(nullable = false, unique = true)
    private String recordedShortsTitle;

    // 녹화한 시간
    //@CreationTimestamp
    @Column(insertable = false)
    private LocalDateTime recordedShortsDate;

    @Column(nullable = false, name = "recorded_shorts_s3link")
    private String recordedShortsS3Link;

    // 유튜브 업로드 URL
    @Column
    private String recordedShortsYoutubeURL;

    // 녹화한 멤버 객체
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @Builder
    public RecordedShorts(String recordedShortsTitle, String recordedShortsS3Link, Member member) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsS3Link = recordedShortsS3Link;
        this.member = member;
    }

}

package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recorded_shorts")
@Entity
public class RecordedShorts {

    // 녹화한 쇼츠 테이블 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordedShortsId;

    // 녹화한 쇼츠 제목
    @Column(nullable = false)
    private String recordedShortsTitle;

    // 녹화한 시간
    @CreationTimestamp
    @Column(nullable = false)
    private Date recordedShortsDate;

    @Column(nullable = false)
    private String recordedShortsS3Link;

    // 유튜브 업로드 url
    @Column
    private String recordedShortsYoutubeUrl;

    // 녹화한 멤버 객체
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public RecordedShorts(String recordedShortsTitle, Date recordedShortsDate, String recordedShortsS3Link, Member member) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsDate = recordedShortsDate;
        this.recordedShortsS3Link = recordedShortsS3Link;
        this.member = member;
    }

}

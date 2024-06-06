package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
    private LocalDateTime recordedShortsDate;

    // 유튜브 업로드 url
    private String recordedShortsYoutubeUrl;

    // 녹화한 멤버 객체
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    @Builder
    public RecordedShorts(String recordedShortsTitle, Member member, Shorts shorts) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.member = member;
        this.shorts = shorts;
    }
}

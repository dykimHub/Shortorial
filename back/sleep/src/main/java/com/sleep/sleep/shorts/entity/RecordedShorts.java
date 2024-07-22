package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recorded_shorts",
        indexes = {@Index(name = "idx_member_date", columnList = "member_no, recorded_shorts_date"),
                @Index(name = "idx_member_youtube", columnList = "member_no, recorded_shorts_youtubeurl")}
)
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
    private OffsetDateTime recordedShortsDate;

    @Column(name = "recorded_shorts_s3key", nullable = false, unique = true)
    private String recordedShortsS3key;

    @Column(name = "recorded_shorts_s3url", nullable = false)
    private String recordedShortsS3URL;

    // 유튜브 업로드 URL
    @Column
    private String recordedShortsYoutubeURL;

    // 녹화한 멤버 객체
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @Builder
    public RecordedShorts(String recordedShortsTitle, String recordedShortsS3key, String recordedShortsS3URL, Member member) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsS3key = recordedShortsS3key;
        this.recordedShortsS3URL = recordedShortsS3URL;
        this.member = member;
    }

}

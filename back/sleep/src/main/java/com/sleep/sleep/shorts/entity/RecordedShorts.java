package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.s3.constants.S3Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recorded_shorts",
        indexes = {@Index(name = "idx_member_date", columnList = "member_no, recorded_shorts_date")}
)
@Entity
public class RecordedShorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int recordedShortsId; // 녹화한 쇼츠 테이블 ID
    @Column(nullable = false)
    private String recordedShortsTitle; // 녹화한 쇼츠 제목
    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime recordedShortsDate; // 녹화한 시간
    @Column(name = "recorded_shorts_s3key", nullable = false, unique = true)
    private String recordedShortsS3key; // 녹화한 쇼츠 s3key
    @Column
    @Enumerated(EnumType.STRING)
    private S3Status status; // s3 업로드 및 처리 상태
    @Column(name = "recorded_shorts_youtubeurl")
    private String recordedShortsYoutubeURL; // 유튜브 업로드 URL
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    @Builder
    public RecordedShorts(String recordedShortsTitle, String recordedShortsS3key, S3Status status, Member member, Shorts shorts) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsS3key = recordedShortsS3key;
        this.status = status;
        this.member = member;
        this.shorts = shorts;
    }
}

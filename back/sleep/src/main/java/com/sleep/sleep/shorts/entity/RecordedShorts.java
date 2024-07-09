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
         //member_no, recorded_shorts_youtubeurl로 복합 인덱스를 만듦
         //member_no, recorded_shorst_youtubeurl 순으로 정렬해서 youtubeurl이 null이 아닌 행을 빠르게 조회 가능
         //recorded_shorts_id, member_no는 각 테이블에서 primary key, recorded_shorts_title은 unique한 값이라 인덱스 설정 되어있음
        indexes = {@Index(name = "idx_member_youtube", columnList = "member_no, recorded_shorts_youtubeurl")})
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

    @Column(nullable = false, unique = true, name = "recorded_shorts_s3key")
    private String recordedShortsS3key;

    @Column(nullable = false, name = "recorded_shorts_s3url")
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

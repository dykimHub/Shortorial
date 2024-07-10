package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@SQLRestriction("is_deleted = false")
@SQLDelete(sql = "update recorded_shorts set is_deleted = true where recorded_shorts_id = ?")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recorded_shorts",
        indexes = {
                // 회원 쇼츠 통계에서 유튜브에 업로드한 쇼츠의 수를 조회할 때 where 절에서 사용하는 두 컬럼을 복합 인덱스로 형성
                @Index(name = "idx_member_youtube", columnList = "member_no, recorded_shorts_youtubeurl"),
                // 회원이 녹화한 쇼츠를 조회할 때 where, orderby 절에서 사용하는 세 컬럼을 복합 인덱스로 형성
                @Index(name = "idx_deleted_member_date", columnList = "is_deleted, member_no, recorded_shorts_date")
        })
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

    @Column
    private boolean isDeleted;

    @Builder
    public RecordedShorts(String recordedShortsTitle, String recordedShortsS3key, String recordedShortsS3URL, Member member) {
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsS3key = recordedShortsS3key;
        this.recordedShortsS3URL = recordedShortsS3URL;
        this.member = member;
    }
}

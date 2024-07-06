package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tried_shorts"
        // member_id를 기준으로 정렬하고 shorts_id를 찾을 수 있도록 복합 인덱스 추가
        //, indexes = {@Index(name = "idx_tried_shorts_composite", columnList = "shorts_id, member_id")}
)
@Entity
public class TriedShorts {

    // 시도한 쇼츠 테이블 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int triedShortsId;

    // 시도한 시간
    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime triedShortsDate;

    // 시도한 쇼츠 객체
    @ManyToOne
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    // 시도한 멤버 객체
    // 시도한 쇼츠를 조회할 때 멤버 정보를 출력할 필요가 없어서 Lazy Fetch로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no")
    private Member member;

    @Builder
    public TriedShorts(Shorts shorts, Member member) {
        this.shorts = shorts;
        this.member = member;
    }

    public void updateTriedShortsDate() {
        OffsetDateTime updatedTriedShortsDate = OffsetDateTime.now(ZoneOffset.UTC);
        this.triedShortsDate = updatedTriedShortsDate;
    }

}

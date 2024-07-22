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
@Table(name = "tried_shorts",
        // 특정 사용자가 시도한 특정 쇼츠는 unique 한 값이므로 복합 unique key 설정
        uniqueConstraints = {@UniqueConstraint(columnNames = {"member_no", "shorts_id"})},
        // 회원이 시도한 쇼츠를 조회할 때 where, orderby 절에서 사용하는 두 컬럼을 복합 인덱스로 형성
        indexes = {@Index(name = "idx_member_date", columnList = "member_no, tried_shorts_date")}
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

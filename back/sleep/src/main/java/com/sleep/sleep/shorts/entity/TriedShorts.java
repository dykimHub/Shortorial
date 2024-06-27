package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tried_shorts")
@Entity
public class TriedShorts {

    // 시도한 쇼츠 테이블 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tryShortsId;

    // 시도한 시간
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime triedShortsDate;

    // 시도한 쇼츠 객체
    @ManyToOne
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    // 시도한 멤버 객체
    // 시도한 쇼츠를 조회할 때 멤버 정보를 출력할 필요가 없어서 Lazy Fetch로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public TriedShorts(Shorts shorts, Member member) {
        this.triedShortsDate = triedShortsDate;
        this.shorts = shorts;
        this.member = member;
    }

    /**
     * 이미 시도한 쇼츠를 다시 시도할 때 시도한 시간을 현재 시간으로 변경함
     *
     * @param triedShortsDate 현재 시간
     */
    public void updateTriedShortsDate(LocalDateTime triedShortsDate) {
        this.triedShortsDate = triedShortsDate;
    }

}

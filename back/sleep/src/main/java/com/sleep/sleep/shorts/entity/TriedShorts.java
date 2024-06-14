package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

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
    @Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime triedShortsDate;

    // 시도한 쇼츠 객체
    @ManyToOne
    @JoinColumn(name = "shorts_id")
    private Shorts shorts;

    // 시도한 멤버 객체
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public TriedShorts(Shorts shorts, Member member) {
        this.shorts = shorts;
        this.member = member;
    }
}

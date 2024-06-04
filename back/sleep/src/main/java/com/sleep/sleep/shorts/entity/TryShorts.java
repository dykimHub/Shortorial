package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "try_shorts")
@Entity
public class TryShorts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tryNo;

    @ManyToOne
    @JoinColumn(name = "shorts_no")
    private Shorts shortsNo;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member memberIndex;

    @Column(nullable = false, updatable = true)
    private LocalDateTime uploadDate;
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Builder
    public TryShorts(Shorts shortsNo, Member memberIndex) {
        this.shortsNo = shortsNo;
        this.memberIndex = memberIndex;
        this.uploadDate = LocalDateTime.now();
    }
}

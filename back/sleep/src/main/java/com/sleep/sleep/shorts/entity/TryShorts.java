package com.sleep.sleep.shorts.entity;

import com.sleep.sleep.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private int tryYn;

}

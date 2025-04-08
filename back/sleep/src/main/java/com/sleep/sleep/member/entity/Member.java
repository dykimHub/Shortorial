package com.sleep.sleep.member.entity;

import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private int memberIndex;
    @Column(unique = true)
    private String memberId;
    @Column(nullable = false)
    private String memberPass;
    @Column(unique = false)
    private String memberNickname;
    @Column(nullable = true)
    private String memberProfile;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "member")
    private List<TriedShorts> triedShortsList;
    @OneToMany(mappedBy = "member")
    private List<RecordedShorts> recordedShortsList;

    @Builder
    public Member(int memberIndex, String memberId, String memberPass, String memberNickname, String memberProfile, MemberRole memberRole) {
        this.memberIndex = memberIndex;
        this.memberId = memberId;
        this.memberPass = memberPass;
        this.memberNickname = memberNickname;
        this.memberProfile = memberProfile;
        this.memberRole = memberRole;
    }

}

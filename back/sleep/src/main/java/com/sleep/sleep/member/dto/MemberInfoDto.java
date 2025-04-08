package com.sleep.sleep.member.dto;

import com.sleep.sleep.member.entity.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoDto {
    int memberIndex;
    String memberId;
    String memberNickname;
    String memberProfile;
    MemberRole memberRole;

    @Builder
    public MemberInfoDto(int memberIndex, String memberId, String memberNickname, String memberProfile, MemberRole memberRole) {
        this.memberIndex = memberIndex;
        this.memberId = memberId;
        this.memberNickname = memberNickname;
        this.memberProfile = memberProfile;
        this.memberRole = memberRole;
    }
}

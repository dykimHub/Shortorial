package com.sleep.sleep.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sleep.sleep.shorts.entity.RecordedShorts;
import com.sleep.sleep.shorts.entity.TriedShorts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no", updatable = false)
    private int memberIndex;

    @Column(unique = true)
    private String memberId;
    @Column(nullable = false)
    private String memberPass;
    @Column(unique = true)
    private String memberNickname;
    @Column(nullable = true)
    private String memberProfile;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @OneToMany(mappedBy = "member")
    private List<TriedShorts> tryShorts;
    @OneToMany(mappedBy = "member")
    private List<RecordedShorts> recordedShorts;

    public static UserDetails of(Member member) {
        return Member.builder()
                .memberId(member.getMemberId())
                .memberPass(member.getMemberPass())
                .memberNickname(member.getMemberNickname())
                .memberProfile(member.getMemberProfile())
                .memberRole(member.getMemberRole())
                .build();
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getMemberRole().name()));
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return getMemberPass();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return getMemberId();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

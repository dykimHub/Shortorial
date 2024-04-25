package com.sleep.sleep.member.entity;

import com.sleep.sleep.shorts.entity.TryShorts;
import com.sleep.sleep.shorts.entity.UploadShorts;
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

    private String memberId;
    private String memberPass;
    private String memberNickname;
    private String memberProfile;
    private MemberRole memberRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getMemberRole().name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return getMemberPass();
    }

    @Override
    public String getUsername() {
        return getMemberId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private String  memberTiktokLink;
    @OneToMany(mappedBy="tryNo")
    private List<TryShorts> tryShorts;
    @OneToMany(mappedBy = "uploadNo")
    private List<UploadShorts> uploadShorts;
}

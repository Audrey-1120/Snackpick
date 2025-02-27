package com.project.snackpick.dto;

import com.project.snackpick.entity.MemberEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private MemberEntity memberEntity;

    public CustomUserDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    // 사용자의 특정 권한에 대해 반환 ex) role값
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return memberEntity.getRole();
            }
        });

        return collection;
    }

    //회원번호 반환
    public int getMemberId() {
        return memberEntity.getMemberId();
    }

    // 비밀번호 반환
    @Override
    public String getPassword() {
        return memberEntity.getPassword();
    }

    // 아이디 반환
    @Override
    public String getUsername() {
        return memberEntity.getId();
    }

    // 닉네임 반환
    public String getNickname() {
        return memberEntity.getNickname();
    }

    // 하단 부분 구현 시 DB에 해당 칼럼 추가

    // 사용자의 아이디가 만료되었는가?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 사용자의 아이디가 잠겼는가?
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
}

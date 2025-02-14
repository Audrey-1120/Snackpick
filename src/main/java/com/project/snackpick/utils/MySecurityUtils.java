package com.project.snackpick.utils;

import com.project.snackpick.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

@Component
public class MySecurityUtils {

    // 로그인한 유저의 닉네임 가져오기
    public String getLoginUserNickName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (((CustomUserDetails) authentication.getPrincipal()).getNickname());
        }
        return null;
    }

    // 로그인한 유저의 Role 가져오기
    public String getLoginUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return role;
    }
}

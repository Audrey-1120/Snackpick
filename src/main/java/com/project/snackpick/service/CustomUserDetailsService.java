package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.repository.MemberRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 ID입니다."));

        if(member.isState()) {
            throw new DisabledException("탈퇴한 회원입니다.");
        }

        return new CustomUserDetails(member);
    }
}

package com.project.snackpick.service;

import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.utils.MyFileUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MyFileUtils myFileUtils;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MyFileUtils myFileUtils) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.myFileUtils = myFileUtils;
    }

    // 회원가입
    @Transactional
    public void signup(MultipartHttpServletRequest multipartHttpServletRequest) {

        String id = multipartHttpServletRequest.getParameter("id");
        String password = multipartHttpServletRequest.getParameter("password");
        String name = multipartHttpServletRequest.getParameter("name");
        String nickname = multipartHttpServletRequest.getParameter("nickname");
        String profileImagePath = myFileUtils.uploadProfileImage(multipartHttpServletRequest);

        Boolean isExist = memberRepository.existsById(id);

        MemberDTO memberDTO = MemberDTO.builder()
                .id(id)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .profileImage(profileImagePath)
                .role("ROLE_ADMIN")
                .build();

        MemberEntity member = memberDTO.toMemberEntity();
        memberRepository.save(member);

    }

    // 아이디 중복 체크
    public Boolean checkId(String id) {
        return memberRepository.existsById(id);
    }


}

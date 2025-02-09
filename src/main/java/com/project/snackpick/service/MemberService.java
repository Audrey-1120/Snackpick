package com.project.snackpick.service;

import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 회원가입
    public void signup(MemberDTO memberDTO) {

        String id = memberDTO.getId();
        String password = memberDTO.getPassword();
        String name = memberDTO.getName();
        String nickname = memberDTO.getNickname();

        Boolean isExist = memberRepository.existsById(id);

        // 이미 가입 되어있는지 확인
        if(isExist) {
            // 이메일 중복이므로 false 전달
            return;
        }

        MemberEntity data = new MemberEntity();

        // 모든 회원가입 하는 사람은 admin 권한 주기
        data.setId(id);
        data.setPassword(bCryptPasswordEncoder.encode(password)); // 비밀번호는 암호화 된 채로 저장되어야 한다.
        data.setName(name);
        data.setNickname(nickname);
        data.setRole("ROLE_ADMIN");

        memberRepository.save(data);

    }

    // 아이디 중복 체크
    public Boolean checkId(String id) {
        // 아이디를 받아서 DB에 해당 아이디가 존재하는지 확인
        Boolean isExist = memberRepository.existsById(id);
        return isExist;
    }
}

package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface MemberService {

    // 회원가입
    Map<String, Object> signup(MemberDTO memberDTO, MultipartFile[] files);

    // 정보 수정
    Map<String, Object> updateProfile(MemberDTO memberDTO, MultipartFile[] files, CustomUserDetails user);

    // 아이디 중복 체크
    Boolean checkId(String id);

    // 프로필 사진 업로드
    void uploadProfileImage(MemberEntity member, MultipartFile[] files) throws IOException;

    // 프로필 사진 삭제
    void deleteProfileImage(MemberEntity member) throws IOException;

    // 회원 정보 조회
    MemberDTO getMemberById(CustomUserDetails user);

    // 비밀번호 인증
    Boolean checkPassword(MemberDTO memberDTO, CustomUserDetails user);

    // 비밀번호 재설정
    Map<String, Object> resetPassword(MemberDTO memberDTO, CustomUserDetails user);
}

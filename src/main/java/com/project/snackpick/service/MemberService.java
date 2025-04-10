package com.project.snackpick.service;

import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface MemberService {

    // 회원가입
    Map<String, Object> signup(MemberDTO memberDTO, MultipartFile[] files);

    // 아이디 중복 체크
    Boolean checkId(String id);

    // 프로필 사진 업로드
    void uploadProfileImage(MemberEntity member, MultipartFile[] files) throws IOException;
}

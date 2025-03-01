package com.project.snackpick.service;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

public interface MemberService {

    // 회원가입
    Map<String, Object> signup(MultipartHttpServletRequest multipartHttpServletRequest);

    // 아이디 중복 체크
    Boolean checkId(String id);


}

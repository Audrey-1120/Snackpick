package com.project.snackpick.controller;

import com.project.snackpick.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 아이디 중복 체크
    @GetMapping("/checkId")
    public ResponseEntity<Map<String, Object>> checkId(String id) {
        Boolean isExist =  memberService.checkId(id);
        return ResponseEntity.ok(Map.of("isExist", isExist));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(MultipartHttpServletRequest multipartHttpServletRequest) {
        Map<String, Object> response = memberService.signup(multipartHttpServletRequest);
        return ResponseEntity.ok(response);
    }
}

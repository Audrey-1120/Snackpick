package com.project.snackpick.controller;

import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return ResponseEntity.ok(Map.of("isExist", memberService.checkId(id)));
    }

    // 회원가입
    @PostMapping("/signup")
    public String signup(MemberDTO memberDTO) {

        memberService.signup(memberDTO);
        // 여기서 true일 경우 가입 진행, false인 경우 이미 등록된 아이디입니다. 라고 전달.
        return "ok";
    }
}

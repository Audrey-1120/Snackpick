package com.project.snackpick.controller;

import com.project.snackpick.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@Controller
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

    // 로그인 실패
    @GetMapping("/loginFail")
    public String loginFail(@RequestParam(value="error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String errorMessage,
                            Model model) {

        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);
        return "member/login";
    }
}

package com.project.snackpick.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberViewController {

    // 로그인 페이지
    @GetMapping("/login.page")
    @Operation(summary = "로그인 페이지 이동", description = "로그인 페이지로 이동")
    public String loginPage(Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "member/login";
    }

    // 회원가입 페이지
    @GetMapping("/signup.page")
    @Operation(summary = "회원가입 페이지 이동", description = "회원가입 페이지로 이동")
    public String signupPage() {
        return "member/signup";
    }
}

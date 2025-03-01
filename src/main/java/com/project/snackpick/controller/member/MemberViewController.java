package com.project.snackpick.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberViewController {

    // 로그인 페이지
    @GetMapping("/login.page")
    public String loginPage() {
       return "member/login";
    }

    // 회원가입 페이지
    @GetMapping("/signup.page")
    public String signupPage() {
        return "member/signup";
    }
}

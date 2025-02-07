package com.project.snackpick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberViewController {

    // 메인 페이지
    @GetMapping({"/index.page", "/"})
    public String mainPage() {
        return "index";
    }

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

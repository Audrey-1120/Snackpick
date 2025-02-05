package com.project.snackpick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /* 메인 페이지 */
    @GetMapping("/index.html")
    public String mainPage() {
        return "index";
    }

    /* 로그인 페이지 */
    @GetMapping("/login.html")
    public String loginPage() {
       return "user/login";
    }

    /* 회원가입 페이지 */
    @GetMapping("/register.html")
    public String register() {
        return "user/register";
    }
}

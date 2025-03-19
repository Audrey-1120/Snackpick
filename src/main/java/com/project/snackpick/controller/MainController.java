package com.project.snackpick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 메인 페이지
    @GetMapping({"/index.page", "/"})
    public String mainPage() {
        return "index";
    }
}

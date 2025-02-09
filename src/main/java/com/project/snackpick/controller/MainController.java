package com.project.snackpick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    // 메인 페이지
    @GetMapping({"/index.page", "/"})
    public String mainPage() {
        return "index";
    }

/*    @GetMapping("/")
    public String mainP() {
        return "Main Controller";
    }*/
}

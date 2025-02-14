package com.project.snackpick.controller;

import com.project.snackpick.utils.MySecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final MySecurityUtils mySecurityUtils;

    public MainController(MySecurityUtils mySecurityUtils) {
        this.mySecurityUtils = mySecurityUtils;
    }

    // 메인 페이지
    @GetMapping({"/index.page", "/"})
    public String mainPage(Model model) {

        String nickname = mySecurityUtils.getLoginUserNickName();
        model.addAttribute("nickname", nickname);
        return "index";
    }

}

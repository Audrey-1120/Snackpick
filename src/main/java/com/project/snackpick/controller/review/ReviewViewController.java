package com.project.snackpick.controller.review;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/review")
public class ReviewViewController {

    // 리뷰 작성 페이지 이동
    @GetMapping("/reviewWrite.page")
    public String reviewWrite() {
        return "review/review-write";
    }
}

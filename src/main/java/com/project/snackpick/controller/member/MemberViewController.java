package com.project.snackpick.controller.member;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@Tag(name = "Member-View", description = "회원 화면")
public class MemberViewController {

    private final MemberService memberService;

    public MemberViewController(MemberService memberService) {
        this.memberService = memberService;
    }

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
    public String signupPage(Authentication authentication) {

        if(authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "member/signup";
    }

    // 마이페이지
    @GetMapping("/profile.page")
    @Operation(summary = "마이페이지 이동", description = "마이페이지로 이동")
    @PreAuthorize("isAuthenticated()")
    public String myPage(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        MemberDTO member = memberService.getMemberById(user);
        model.addAttribute("member", member);
        return "member/profile";
    }

    // 정보 수정 페이지
    @GetMapping("/profile-update.page")
    @Operation(summary = "정보수정 페이지 이동", description = "정보수정 페이지 이동")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails user, Model model) {

        MemberDTO member= memberService.getMemberById(user);
        model.addAttribute("member", member);
        return "member/profile-update";
    }
}

package com.project.snackpick.controller.member;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/member")
@Tag(name = "Member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 아이디 중복 체크
    @GetMapping("/checkId")
    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 확인")
    @ApiResponse(responseCode = "200", description = "성공")
    public ResponseEntity<Map<String, Object>> checkId(String id) {

        Boolean isExist =  memberService.checkId(id);
        return ResponseEntity.ok(Map.of("isExist", isExist));
    }

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 회원 데이터 추가")
    @ApiResponse(responseCode = "200", description = "성공")
    @ApiResponse(responseCode = "400", description = "중복된 회원이 가입함")
    public ResponseEntity<Map<String, Object>> signup(@ModelAttribute MemberDTO memberDTO,
                                                      @RequestPart MultipartFile[] files) {

        Map<String, Object> response = memberService.signup(memberDTO, files);
        return ResponseEntity.ok(response);
    }

    // 회원 정보 수정
    @PutMapping("/updateProfile")
    @Operation(summary = "정보 수정", description = "회원 정보 수정")
    public ResponseEntity<Map<String, Object>> updateProfile(@ModelAttribute MemberDTO memberDTO,
                                                             @RequestPart MultipartFile[] files,
                                                             @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = memberService.updateProfile(memberDTO, files, user);
        return ResponseEntity.ok(response);
    }

    // 로그아웃
    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃")
    @PreAuthorize("isAuthenticated()")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    // 로그인 실패
    @GetMapping("/loginFail")
    @Operation(summary = "로그인 실패", description = "로그인 실패 시 에러 메시지 반환")
    public String loginFail(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String errorMessage,
                            Model model) {

        model.addAttribute("error", error);
        model.addAttribute("errorMessage", errorMessage);
        return "member/login";
    }

    // 비밀번호 검증
    @PostMapping("/checkPassword")
    @Operation(summary = "비밀번호 검증", description = "회원 비밀번호가 맞는지 검증")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkPassword(@RequestBody MemberDTO memberDTO,
                                                             @AuthenticationPrincipal CustomUserDetails user) {

        Boolean isMatch = memberService.checkPassword(memberDTO, user);
        return ResponseEntity.ok(Map.of("isMatch", isMatch));
    }

    // 비밀번호 재설정
    @PutMapping("/resetPassword")
    @Operation(summary = "비밀번호 재설정", description = "회원 비밀번호 재설정")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody MemberDTO memberDTO,
                                                             @AuthenticationPrincipal CustomUserDetails user) {

        Map<String, Object> response = memberService.resetPassword(memberDTO, user);
        return ResponseEntity.ok(response);
    }
}

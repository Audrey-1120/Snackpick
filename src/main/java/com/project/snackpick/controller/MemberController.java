package com.project.snackpick.controller;

import com.project.snackpick.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    public ResponseEntity<Map<String, Object>> signup(MultipartHttpServletRequest multipartHttpServletRequest) {
        Map<String, Object> response = memberService.signup(multipartHttpServletRequest);
        return ResponseEntity.ok(response);
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
}

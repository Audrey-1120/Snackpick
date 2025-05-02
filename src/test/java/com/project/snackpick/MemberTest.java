package com.project.snackpick;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.service.MemberService;
import com.project.snackpick.service.ReviewService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@DisplayName("회원 테스트")
public class MemberTest {

    private final MemberService memberService;
    @Autowired
    private ReviewService reviewService;

    @Autowired
    public MemberTest(MemberService memberService) {
        this.memberService = memberService;
    }

    @BeforeEach
    public void before() {
        System.out.println("Test Before");
    }

    @AfterEach
    public void after() {
        System.out.println("Test After");
    }

    @Test
    @DisplayName("회원 가입")
    public void signup() {

        MemberDTO member = MemberDTO.builder()
                .id("test1")
                .password("Asdfg12345@")
                .name("사용자1")
                .nickname("사용자닉네임1")
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "profileImage", // 필드명
                "test1.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8)
        );

        MultipartFile[] files = {file1};

        Map<String, Object> map = memberService.signup(member, files);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        assertTrue((Boolean)map.get("success"));
    }

    @Test
    @DisplayName("정보 수정")
    public void updateProfile() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        MemberDTO memberDTO = MemberDTO.builder()
                .name("수정된 이름")
                .nickname("수정된 닉네임")
                .build();

        MockMultipartFile file1 = new MockMultipartFile(
                "profileImage", // 필드명
                "test1.jpg", // 파일명
                "image/jpeg", // MIME 타입
                "image data".getBytes(StandardCharsets.UTF_8)
        );

        MultipartFile[] files = {file1};

        CustomUserDetails user = new CustomUserDetails(member);

        Map<String, Object> map = memberService.updateProfile(memberDTO, files, user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        assertTrue((Boolean)map.get("success"));

    }

    @Test
    @DisplayName("회원정보 조회")
    public void getMemberProfile() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        CustomUserDetails user = new CustomUserDetails(member);

        MemberDTO memberDTO = memberService.getMemberById(user);
        System.out.println("회원 ID: " + memberDTO.getMemberId());
        System.out.println("닉네임 : " + memberDTO.getNickname());

        assertNotNull(memberDTO);
    }

    @Test
    @DisplayName("회원 탈퇴")
    public void leave() {

        MemberEntity member = MemberEntity.builder()
                .memberId(1)
                .id("mintii92")
                .name("김민티")
                .nickname("민티927")
                .build();

        CustomUserDetails user = new CustomUserDetails(member);

        Map<String, Object> map = memberService.leave(user);

        System.out.println("완료 메시지: " + String.valueOf(map.get("message")));
        System.out.println("리다이렉트 경로" + String.valueOf(map.get("redirectUrl")));

        assertTrue((Boolean)map.get("success"));

    }
}

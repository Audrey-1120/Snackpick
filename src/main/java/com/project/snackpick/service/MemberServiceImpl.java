package com.project.snackpick.service;

import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.utils.MyFileUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.Map;

@Transactional
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MyFileUtils myFileUtils;

    public MemberServiceImpl(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MyFileUtils myFileUtils) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.myFileUtils = myFileUtils;
    }

    // 회원가입
    @Override
    public Map<String, Object> signup(MultipartHttpServletRequest multipartHttpServletRequest) {

        String id = multipartHttpServletRequest.getParameter("id");
        String password = multipartHttpServletRequest.getParameter("password");
        String name = multipartHttpServletRequest.getParameter("name");
        String nickname = multipartHttpServletRequest.getParameter("nickname");
        String profileImagePath = myFileUtils.uploadProfileImage(multipartHttpServletRequest);

        MemberDTO memberDTO = MemberDTO.builder()
                .id(id)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .profileImage(profileImagePath)
                .role("ROLE_USER")
                .build();

        MemberEntity member = memberDTO.toMemberEntity();

        // 회원 중복 예외 발생 시 처리
        try {
            // 성공할 경우 success: true, message, redirectUrl
            memberRepository.save(member);
            return Map.of("success", true
                    , "message", "회원가입이 완료되었습니다."
                    , "redirectUrl", "/member/login.page");

        } catch(DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.MEMBER_DUPLICATE);
        } catch(Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    // 아이디 중복 체크
    @Override
    public Boolean checkId(String id) {
        return memberRepository.existsById(id);
    }
}

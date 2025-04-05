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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    @Transactional
    public Map<String, Object> signup(MemberDTO memberDTO, MultipartFile[] files) {

        MemberEntity member = MemberEntity.toMemberEntity(memberDTO);

        member.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));
        member.setRole("ROLE_USER");

        try {

            memberRepository.save(member);
            uploadProfileImage(member, files);

        } catch(DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.MEMBER_DUPLICATE);
        } catch(IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("프로필"));
        } catch(Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        return Map.of("success", true
                , "message", "회원가입이 완료되었습니다."
                , "redirectUrl", "/member/login.page");
    }

    // 아이디 중복 체크
    @Override
    @Transactional(readOnly = true)
    public Boolean checkId(String id) {
        return memberRepository.existsById(id);
    }

    // 프로필 사진 업로드
    @Override
    public void uploadProfileImage(MemberEntity member, MultipartFile[] files) throws IOException {

        if(files == null || files.length == 0 || files[0].isEmpty()) {
            return;
        }

        List<String> imageUrlList = myFileUtils.getImageUrlList(files, "profile");
        member.setProfileImage(imageUrlList.get(0));

        myFileUtils.uploadImage(files, imageUrlList, "profile");
    }
}

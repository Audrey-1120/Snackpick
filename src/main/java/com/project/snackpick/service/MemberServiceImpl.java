package com.project.snackpick.service;

import com.project.snackpick.dto.CustomUserDetails;
import com.project.snackpick.dto.MemberDTO;
import com.project.snackpick.dto.ReviewAction;
import com.project.snackpick.dto.ReviewDTO;
import com.project.snackpick.entity.MemberEntity;
import com.project.snackpick.entity.ReviewEntity;
import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.repository.CommentRepository;
import com.project.snackpick.repository.MemberRepository;
import com.project.snackpick.repository.ReviewRepository;
import com.project.snackpick.utils.MyFileUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MyFileUtils myFileUtils;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public MemberServiceImpl(MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MyFileUtils myFileUtils, ReviewRepository reviewRepository, ReviewService reviewService, CommentRepository commentRepository, CommentService commentService) {
        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.myFileUtils = myFileUtils;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
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
        }

        return Map.of("success", true
                , "message", "회원가입이 완료되었습니다."
                , "redirectUrl", "/member/login.page");
    }

    // 정보 수정
    @Override
    @Transactional
    public Map<String, Object> updateProfile(MemberDTO memberDTO, MultipartFile[] files, CustomUserDetails user) {

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원 ")));

        member.setName(memberDTO.getName());
        member.setNickname(memberDTO.getNickname());

        boolean isFileChange = memberDTO.isFileChanged();
        String profileType = memberDTO.getProfileType();

        if(isFileChange) {

            if(profileType.equals("non-default")) {
                deleteProfileImage(member);
                uploadProfileImage(member, files);
            } else {
                deleteProfileImage(member);
            }
        }

        return Map.of("success", true
                , "message", "회원정보가 수정되었습니다."
                , "redirectUrl", "/member/profile.page");
    }

    // 회원 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberDTO getMemberById(CustomUserDetails user) {

        Object[] result = (Object[]) memberRepository.findMemberByMemberId(user.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원")))[0];

        MemberEntity member = (MemberEntity) result[0];
        Long reviewCount = (Long) result[1];
        Long commentCount = (Long) result[2];

        return new MemberDTO(member, reviewCount, commentCount);
    }

    // 회원 탈퇴
    @Override
    @Transactional
    public Map<String, Object> leave(CustomUserDetails user) {

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원 ")));

        List<ReviewEntity> reviewList = reviewRepository.findAllReviewListByMemberId(user.getMemberId());

        if(!reviewList.isEmpty()) {

            reviewService.deleteReviewList(reviewList);

            for(ReviewEntity review : reviewList) {
                reviewService.updateProductStats(review.getProductEntity()
                                                , review
                                                , new ReviewDTO()
                                                , ReviewAction.DELETE);
            }
        }

        List<Integer> commentIdList = commentRepository.findAllCommentIdListByMemberId(user.getMemberId());
        commentService.deleteCommentList(commentIdList);

        member.setState(true);
        return Map.of("success", true,
                    "message", "회원탈퇴가 완료되었습니다.",
                    "redirectUrl", "/member/logout");
    }

    // 아이디 중복 체크
    @Override
    @Transactional(readOnly = true)
    public Boolean checkId(String id) {
        return memberRepository.existsById(id);
    }

    // 프로필 사진 업로드
    @Override
    public void uploadProfileImage(MemberEntity member, MultipartFile[] files) {

        if(files == null || files.length == 0 || files[0].isEmpty()) {
            return;
        }

        try {
            List<String> imageUrlList = myFileUtils.getImageUrlList(files, "profile");
            member.setProfileImage(imageUrlList.get(0));

            myFileUtils.uploadImage(files, imageUrlList, "profile");

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL,
                    ErrorCode.FILE_UPLOAD_FAIL.formatMessage("회원 프로필"));
        }
    }

    // 프로필 사진 삭제
    @Override
    public void deleteProfileImage(MemberEntity member) {

        String profileImage = member.getProfileImage();

        if(profileImage == null) {
            return;
        }

        try {
            member.setProfileImage(null);
            myFileUtils.deleteImage(List.of(profileImage));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_ERROR,
                    ErrorCode.SERVER_ERROR.formatMessage("프로필 이미지 삭제"));
        }
    }

    // 비밀번호 검증
    @Override
    @Transactional(readOnly = true)
    public Boolean checkPassword(MemberDTO memberDTO, CustomUserDetails user) {

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원")));

        return bCryptPasswordEncoder.matches(memberDTO.getPassword(), member.getPassword());
    }

    // 비밀번호 재설정
    @Override
    @Transactional
    public Map<String, Object> resetPassword(MemberDTO memberDTO, CustomUserDetails user) {

        MemberEntity member = memberRepository.findById(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ENTITY,
                        ErrorCode.NOT_FOUND_ENTITY.formatMessage("회원")));

        String currentPassword = member.getPassword();
        String newPassword = memberDTO.getNewPassword();

        if(!bCryptPasswordEncoder.matches(memberDTO.getPassword(), currentPassword)) {
            return Map.of("success", false
                        ,"message", "현재 비밀번호가 올바르지 않습니다.");
        }

        if(bCryptPasswordEncoder.matches(newPassword, currentPassword)) {
            return Map.of("success", false
                        , "message", "새 비밀번호와 현재 비밀번호를 다르게 설정해주세요.");
        }

        member.setPassword(bCryptPasswordEncoder.encode(newPassword));

        return Map.of("success", true,
                    "message", "비밀번호가 재설정되었습니다.",
                    "redirectUrl", "/member/logout");
    }
}
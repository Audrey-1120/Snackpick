package com.project.snackpick.utils;

import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MyFileUtils {

    private static final String BASE_URL = "http://localhost:8080/images";
    private static final String FILE_STORAGE_PATH = "/home/snackpickImage";

    // 현재 날짜
    public static final LocalDate TODAY = LocalDate.now();

    // 이미지 업로드 경로 반환
    public String getUploadPath(String path) {
        return FILE_STORAGE_PATH + "/" + path + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(TODAY);
    }

    // 브라우저 접근 URL 반환
    public String getImageUrl(String path) {
        return BASE_URL + "/" + path + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(TODAY);
    }

    // 저장 파일명 반환
    public String getFileSystemName(String originalFilename) {
        String extName = null;
        if(originalFilename.endsWith(".tar.gz")) {
            extName = ".tar.gz";
        } else {
            extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extName;
    }

    // 프로필 사진 첨부
    public String uploadProfileImage(MultipartHttpServletRequest request) {

        MultipartFile profile = request.getFile("profileImage");
        String profileImagePath = "";

        if(profile != null && !profile.isEmpty() && profile.getSize() > 0) {
            StringBuilder builder = new StringBuilder();
            String uploadPath = getUploadPath("profile");
            String imageUrl = getImageUrl("profile");

            File dir = new File(uploadPath);

            if(!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = profile.getOriginalFilename();
            String fileSystemName = getFileSystemName(originalFilename);

            profileImagePath = builder.append(imageUrl)
                                        .append("/")
                                        .append(fileSystemName).toString();
            builder.setLength(0);

            File file = new File(dir, fileSystemName);

            try{
                profile.transferTo(file);
            } catch (Exception e) {
                throw new CustomException(ErrorCode.SERVER_ERROR,
                        ErrorCode.SERVER_ERROR.formatMessage("프로필 사진 업로드"));
            }
        }
        return profileImagePath;
    }

    // 리뷰 사진 업로드
    public ArrayList<String> uploadReviewImage(MultipartFile[] reviewImageList) {

        ArrayList<String> reviewImagePath = new ArrayList<>();

        if(reviewImageList != null && reviewImageList.length > 0) {

            for(MultipartFile reviewImage : reviewImageList) {

                StringBuilder builder = new StringBuilder();
                String uploadPath = getUploadPath("review");
                String imageUrl = getImageUrl("review");

                File dir = new File(uploadPath);

                if(!dir.exists()) {
                    dir.mkdirs();
                }

                String originalFilename = reviewImage.getOriginalFilename();
                String fileSystemName = getFileSystemName(originalFilename);

                reviewImagePath.add(builder.append(imageUrl)
                                            .append("/")
                                            .append(fileSystemName).toString());
                builder.setLength(0);

                File file = new File(dir, fileSystemName);

                try{
                    reviewImage.transferTo(file);
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.SERVER_ERROR,
                            ErrorCode.SERVER_ERROR.formatMessage("리뷰 사진 업로드"));
                }
            }
        }
        return reviewImagePath;
    }

    // 리뷰 사진 삭제
    public boolean deleteExistImage(List<String> imageURlList) {

        // 경로 변환
        List<String> imagePathList = convertUrlToPath(imageURlList);
        boolean result = true;

        for (String imagePath : imagePathList) {
            File file = new File(imagePath);
            if(file.exists()) {
                if(file.delete()) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                return false;
            }
        }
        return result;
    }

    // 파일 경로 브라우저 -> WSL2 내부 경로로 변환
    public static List<String> convertUrlToPath(List<String> imageUrlList) {
        return imageUrlList.stream()
                .filter(url -> url.startsWith(BASE_URL)) // BASE_URL로 시작하는 것만 처리한다.
                .map(url -> FILE_STORAGE_PATH + url.substring(BASE_URL.length())) // 파일 경로로 변환한다...
                .toList();
    }
}

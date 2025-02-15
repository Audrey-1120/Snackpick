package com.project.snackpick.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class MyFileUtils {

    // 현재 날짜
    public static final LocalDate TODAY = LocalDate.now();

    // 프로필 사진 경로 반환
    public String getProfilePath() {
        String homeDir = System.getProperty("user.home");
        return homeDir + "/profile" + DateTimeFormatter.ofPattern("/yyyy/MM/dd").format(TODAY);
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
            String uploadPath = getProfilePath();

            File dir = new File(uploadPath);

            if(!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = profile.getOriginalFilename();
            String fileSystemName = getFileSystemName(originalFilename);

            profileImagePath = builder.append("<img src=\"").append(request.getContextPath()).append(uploadPath).append("/") .append(fileSystemName).append("\">").toString();
            builder.setLength(0);

            File file = new File(dir, fileSystemName);

            try{
                profile.transferTo(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profileImagePath;
    }

}

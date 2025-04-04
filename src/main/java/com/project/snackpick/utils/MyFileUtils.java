package com.project.snackpick.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class MyFileUtils {

    private static final String BASE_URL = "/images";
    private static final String FILE_STORAGE_PATH = "/home/snackpickImage";

    private static final Logger log = LoggerFactory.getLogger(MyFileUtils.class);

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

        if(originalFilename == null || originalFilename.isBlank()) {
            originalFilename = UUID.randomUUID().toString() + ".jpg";
        }

        String extName;
        if(originalFilename.endsWith(".tar.gz")) {
            extName = ".tar.gz";
        } else {
            extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + extName;
    }

    // 파일 리스트 빈값 체크
    public boolean isFilesEmpty(MultipartFile[] files) {
        return files == null || files.length == 0;
    }

    // 브라우저 접근 URL 리스트 반환
    public List<String> getImageUrlList(MultipartFile[] files, String path) {

        List<String> imageUrlList = new ArrayList<>();

        if(isFilesEmpty(files)) return imageUrlList;

        for(MultipartFile file : files) {

            StringBuilder builder = new StringBuilder();

            String imageUrl = getImageUrl(path);
            String originalFilename = file.getOriginalFilename();
            String fileSystemName = getFileSystemName(originalFilename);

            imageUrlList.add(builder.append(imageUrl)
                    .append("/")
                    .append(fileSystemName).toString());

            builder.setLength(0);

        }
        return imageUrlList;
    }

    // 이미지 업로드
    public void uploadImage(MultipartFile[] files, List<String> imageUrlList, String path) throws IOException {

        if(isFilesEmpty(files)) return;

        for(int i = 0; i < files.length; i++) {

            String uploadPath = getUploadPath(path);
            String browserUrl = imageUrlList.get(i);
            File dir = new File(uploadPath);

            if(!dir.exists()) {
                dir.mkdirs();
            }

            String fileSystemName = browserUrl.substring(browserUrl.lastIndexOf("/") + 1);
            File uploadFile = new File(dir, fileSystemName);

            try{
                files[i].transferTo(uploadFile);
            } catch (Exception e) {
                log.error("이미지 업로드 실패: {}", uploadFile);
                throw e;
            }
        }
    }

    // 이미지 삭제
    public void deleteImage(List<String> imageUrlList) {

        List<String> imagePathList = convertUrlToPath(imageUrlList);

        for (String imagePath : imagePathList) {
            try {
                File file = new File(imagePath);
                if(file.exists()) {
                    boolean deleted = file.delete();
                    if(!deleted) {
                        log.error("이미지 삭제 실패: {}", imagePath);
                    }
                } else {
                    log.warn("파일 없음: {}", imagePath);
                }
            } catch (Exception e) {
                log.error("이미지 삭제 중 에러 발생: {}", imagePath, e);
            }
        }
    }

    // 이미지 실제 업로드 경로 변환
    public static List<String> convertUrlToPath(List<String> imageUrlList) {
        return imageUrlList.stream()
                .filter(url -> url.startsWith(BASE_URL))
                .map(url -> FILE_STORAGE_PATH + url.substring(BASE_URL.length()))
                .toList();
    }
}

package com.project.snackpick.utils;

import com.project.snackpick.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MyPageUtils {

    // pageDTO로 변환
    public static <T> PageDTO<T> toPageDTO(Page<T> page) {

        int currentPage; // 현재 페이지 번호

        int pagePerBlock = 5; // 한 블록에 표시할 페이지 링크의 개수
        int totalPage; // 전체 페이지 개수
        int beginPage; // 한 블록에 표시할 페이지 링크 시작 번호
        int endPage; // 한 블록에 표시할 페이지 링크 종료 번호

        currentPage = page.getNumber() + 1;
        totalPage = page.getTotalPages();

        beginPage = ((currentPage - 1) / pagePerBlock) * pagePerBlock + 1;
        endPage = Math.min(totalPage, beginPage + pagePerBlock - 1);

        return new PageDTO<>(
                page.getContent(),
                currentPage,
                totalPage,
                beginPage,
                endPage
        );
    }
}

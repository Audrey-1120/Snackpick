package com.project.snackpick.utils;

import com.project.snackpick.dto.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class MyPageUtils {

    public <T> PageDTO<T> toPageDTO(Page<T> page) {

        int currentPage;
        int pagePerBlock = 5;
        int totalPage;
        int beginPage;
        int endPage;

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

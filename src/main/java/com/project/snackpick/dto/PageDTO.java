package com.project.snackpick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    private List<T> contents; // 조회 데이터
    private int currentPage; // 현재 페이지
    private int totalPage; // 전체 페이지
    private int beginPage; // 시작 페이지
    private int endPage; // 끝 페이지

}

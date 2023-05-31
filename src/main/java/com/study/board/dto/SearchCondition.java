package com.study.board.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * 검색조건을 담은 DTO
 */
@Getter
@Setter
@Builder
public class SearchCondition {

    private final Integer offset;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String category;
    private final String search;
    private final Integer limit;
}

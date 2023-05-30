package com.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 검색조건을 담은 DTO
 */
@Getter
@AllArgsConstructor
public class SearchCondition {

    private final Integer offset;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String category;
    private final String search;
    private final int limit;
}

package com.study.board.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 검색조건을 담은 DTO
 */
@Getter
@Setter
@Builder
public class SearchCondition {

    private final Integer offset;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String category;
    private final String search;
    private final Integer limit;
}

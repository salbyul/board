package com.study.board.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Board {

    private Long boardId;
    private Long categoryId;
    private String writer;
    private String password;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime generationTimestamp;
    private LocalDateTime modificationTimestamp;
}

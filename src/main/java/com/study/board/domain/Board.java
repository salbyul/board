package com.study.board.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Board {

    private Long boardId;
    private Long category_id;
    private String writer;
    private String password;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}

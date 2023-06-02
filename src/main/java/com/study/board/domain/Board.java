package com.study.board.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Board {

//    게시글 생성 시 유효성 검사 필드
    public static final String WRITER_ERROR = "Writer Error";
    public static final String PASSWORD_ERROR = "Password Error";
    public static final String TITLE_ERROR = "Title Error";
    public static final String CONTENT_ERROR = "Content Error";

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

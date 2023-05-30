package com.study.board.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {

    private Long commentId;
    private String writer;
    private String content;
    private LocalDateTime generationTimestamp;
    private Long boardId;
}

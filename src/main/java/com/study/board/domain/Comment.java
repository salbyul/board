package com.study.board.domain;

import java.time.LocalDateTime;

public class Comment {

    private Long commentId;
    private String writer;
    private String content;
    private LocalDateTime createdDate;
    private Long boardId;
}

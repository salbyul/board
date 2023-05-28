package com.study.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public abstract class BoardDTO {

    private Long boardId;
    private String category;
    private String writer;
    private String password;
    private String title;
    private String content;
    private Integer views;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public void asdf(String a, String b) {
        category = a;
        writer = b;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardListDTO extends BoardDTO {

    }

}

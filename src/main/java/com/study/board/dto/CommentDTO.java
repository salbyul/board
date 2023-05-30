package com.study.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public abstract class CommentDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentDetailDTO extends CommentDTO{

        private String writer;
        private String content;
        private LocalDateTime generationTimestamp;
    }
}

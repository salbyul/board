package com.study.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public abstract class CommentDTO {

    /**
     * Detail 페이지에 표시할 CommentDTO 객체
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentDetailDTO extends CommentDTO {

        private String writer;
        private String content;
        private LocalDateTime generationTimestamp;
    }

    /**
     * 댓글을 저장하기 위한 값을 담은 객체
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentSaveDTO extends CommentDTO {

        private String writer;
        private String content;
        private LocalDateTime generationTimestamp = LocalDateTime.now();
    }

}

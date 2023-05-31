package com.study.board.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public abstract class BoardDTO {

    public static final String BOARD_LIST_DTO = "boardListDTO";
    public static final String BOARD_DETAIL_DTO = "boardDetailDTO";

    /**
     * 리스트 페이지에 표시할 Board의 정보들을 담은 DTO
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BoardListDTO extends BoardDTO {
        List<BoardSmallDTO> boardList;
        List<String> categoryList;
        Integer boardCounts;
    }

    /**
     * 메인페이지에 게시글 리스트로서 표현할 객체
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardSmallDTO extends BoardDTO {
        private Long boardId;
        private String category;
        private Boolean hasFile = false;
        private String title;
        private String writer;
        private int views;
        private LocalDateTime generationTimestamp;
        private LocalDateTime modificationTimestamp;

        public void setCategory(String category) {
            this.category = category;
        }

        public void setHasFile(boolean hasFile) {
            this.hasFile = hasFile;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardDetailDTO extends BoardDTO {
        private String category;
        private String title;
        private String writer;
        private String content;
        private int views;
        private LocalDateTime generationTimestamp;
        private LocalDateTime modificationTimestamp;
        private List<CommentDTO.CommentDetailDTO> commentDetailDTOs;
        private List<String> fileNames;

        public void assembleCategoryAndCommentsAndFileNames(String category, List<CommentDTO.CommentDetailDTO> commentDetailDTOs, List<String> fileNames) {
            this.category = category;
            this.commentDetailDTOs = commentDetailDTOs;
            this.fileNames = fileNames;
        }
    }
}

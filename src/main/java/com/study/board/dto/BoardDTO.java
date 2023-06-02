package com.study.board.dto;

import com.study.board.SHA256Encoder;
import lombok.*;

import java.security.NoSuchAlgorithmException;
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

    /**
     * 디테일 페이지에 표현할 정보들을 담은 객체
     */
    @Getter
    @Setter
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

    /**
     * 게시글 생성 시 입력한 값들이 담긴 객체
     */
    @Getter
    @Setter
    public static class BoardCreationDTO extends BoardDTO {
        private String category;
        private String writer;
        private String password;
        private String title;
        private String content;

        public void encryptPassword(SHA256Encoder encoder) throws NoSuchAlgorithmException {
            this.password = encoder.encrypt(password);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoardSaveDTO extends BoardDTO {
        private Long boardId;
        private Long categoryId;
        private String writer;
        private String password;
        private String title;
        private String content;
        private Integer views;
        private LocalDateTime generationTimestamp;

        /**
         * boardCreationDTO를 이용해 BoardSaveDTO를 생성해 리턴한다.
         *
         * @param boardCreationDTO
         * @param categoryId
         * @return
         */
        public static BoardSaveDTO transformBoardCreationDTOIntoBoardSaveDTO(BoardCreationDTO boardCreationDTO, Long categoryId) {
            return new BoardSaveDTO(null, categoryId, boardCreationDTO.getWriter(), boardCreationDTO.getPassword(), boardCreationDTO.getTitle(), boardCreationDTO.getContent(), 0, LocalDateTime.now());
        }
    }

    /**
     * 게시글 생성 시 유효성 검사에 실패한 경우 기존에 입력한 값들을 담은 객체
     */
    @Getter
    @Setter
    public static class BoardFailedCreationDTO extends BoardDTO {
        private String category;
        private String writer;
        private String title;
        private String content;
    }
}

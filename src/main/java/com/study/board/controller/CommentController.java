package com.study.board.controller;

import com.study.board.dto.CommentDTO;
import com.study.board.service.BoardService;
import com.study.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.study.board.dto.BoardDTO.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final CommentService commentService;
    private final BoardService boardService;

    /**
     * 게시글의 Detail을 표현할 페이지와 연결된다.
     * Post요청으로 CommentSaveDTO를 받아 DB에 저장시킨후 Detail 페이지로 이동한다.
     *
     * @param commentSaveDTO
     * @param boardId
     * @param model
     * @return
     */
    @PostMapping("/comment/save")
    public String save(@ModelAttribute CommentDTO.CommentSaveDTO commentSaveDTO, @RequestParam("i") Long boardId, Model model) {
        commentService.save(commentSaveDTO, boardId);
        model.addAttribute(BOARD_DETAIL_DTO, boardService.getBoardDetailDTO(boardId));
        return "detail";
    }
}

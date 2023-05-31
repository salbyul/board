package com.study.board.service;

import com.study.board.dto.CommentDTO;
import com.study.board.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentMapper commentMapper;

    /**
     * CommentSaveDTO 객체를 받아 DB에 저장한다.
     * @param commentSaveDTO
     */
    public void save(CommentDTO.CommentSaveDTO commentSaveDTO, Long boardId) {
        commentMapper.save(commentSaveDTO, boardId);
    }
}

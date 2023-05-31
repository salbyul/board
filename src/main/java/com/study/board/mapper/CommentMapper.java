package com.study.board.mapper;

import com.study.board.domain.Comment;
import com.study.board.dto.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * Board의 primary key를 이용해 Comment 객체들을 List에 담아 리턴한다.
     * @param boardId
     * @return
     */
    List<Comment> findCommentsByBoardId(Long boardId);

    /**
     * CommentSaveDTO를 이용해 Comment를 저장한다.
     * @param commentSaveDTO
     */
    void save(CommentDTO.CommentSaveDTO commentSaveDTO, Long boardId);
}

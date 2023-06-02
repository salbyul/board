package com.study.board.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static com.study.board.dto.BoardDTO.*;

/**
 * 게시글 생성페이지에 전달될 값을 담은 객체
 */
@Getter
@AllArgsConstructor
public class BoardCreateResponse {

    private BoardFailedCreationDTO boardFailedCreationDTO;
    private List<String> categories;
}

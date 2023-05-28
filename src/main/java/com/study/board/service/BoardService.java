package com.study.board.service;

import com.study.board.dto.BoardDTO;
import com.study.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardMapper boardMapper;

    public BoardDTO getBoardList(Map<String, String[]> parameterMap) {
        return null;
    }
}

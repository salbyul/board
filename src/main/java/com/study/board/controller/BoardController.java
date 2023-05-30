package com.study.board.controller;

import com.study.board.dto.SearchCondition;
import com.study.board.service.BoardService;
import com.study.board.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import static com.study.board.dto.BoardDTO.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    /**
     * 메인 리스트 페이지와 연결된다.
     * HttpServletRequest에서 검색조건을 찾아낸 뒤 이에 맞는 Board 객체들을 DB에서 찾아서 Model에 담는다.
     * index.html로 이동한다.
     *
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {

        BoardListDTO boardListDTO = boardService.getBoardListDTO(getSearchCondition(request));
        model.addAttribute(BOARD_LIST_DTO, boardListDTO);
        return "index";
    }

    /**
     * 게시글의 Detail을 표현할 페이지와 연결된다.
     * BoardService에서 BoardDetailDTO를 받아 detail.html로 이동한다.
     *
     * @param boardId
     * @param model
     * @return
     */
    @GetMapping("/detail")
    public String detail(@RequestParam("i") Long boardId, Model model) {
        model.addAttribute(BOARD_DETAIL_DTO, boardService.getBoardDetailDTO(boardId));
        boardService.updateViews(boardId);
        return "detail";
    }

    /**
     * HttpServletRequest를 이용해 SearchCondition 객체를 만들어 리턴한다.
     *
     * @param request
     * @return
     */
    private SearchCondition getSearchCondition(HttpServletRequest request) {
        RequestUtil requestUtil = new RequestUtil(request);

        return new SearchCondition(requestUtil.getPage(),
                requestUtil.getStartDate(),
                requestUtil.getEndDate(),
                requestUtil.getCategory(),
                requestUtil.getSearch(),
                10);
    }
}

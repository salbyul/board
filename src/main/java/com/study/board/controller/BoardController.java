package com.study.board.controller;

import com.study.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    public String List(HttpServletRequest request, Model model) {
        model.addAttribute("boardDto", boardService.getBoardList(request.getParameterMap()));
        return "index";
    }
}

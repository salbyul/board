package com.study.board.controller;

import com.study.board.dto.SearchCondition;
import com.study.board.service.BoardService;
import com.study.board.service.FileService;
import com.study.board.util.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static com.study.board.dto.BoardDTO.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final FileService fileService;

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
     * fileName을 매개변수로 받아 해당 파일을 찾고 전송을 한다.
     *
     * @param fileName
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void download(@RequestParam("file_name") String fileName, HttpServletResponse response) throws IOException {
        byte[] fileByte = fileService.getFileByte(fileName);
        setResponseToTransferFile(response, fileName);
        transferFile(response, fileByte);
    }

    /**
     * 해당 Board를 제거한다.
     * 그 후 메인 페이지로 리다이렉트한다.
     *
     * @param request
     * @param response
     * @param boardId
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @PostMapping("/delete")
    public String deleteBoard(HttpServletRequest request, HttpServletResponse response, @RequestParam("i") Long boardId, @RequestParam String password) throws NoSuchAlgorithmException, IOException {
        boardService.deleteBoard(boardId, password);
        redirect(request, response, "/");
        return null;
    }

    /**
     * 검색조건을 파라미터로 붙여 해당 주소로 리다이렉트한다.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        RequestUtil requestUtil = new RequestUtil(request);
        String redirectPath = requestUtil.assembleParameter(path);
        response.sendRedirect(redirectPath);
    }

    /**
     * 실제로 파일전송을 시작한다.
     *
     * @param response
     * @param fileByte
     * @throws IOException
     */
    private void transferFile(HttpServletResponse response, byte[] fileByte) throws IOException {
        response.getOutputStream().write(fileByte);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * Response의 Content Type를 세팅하고 Header를 추가해준다.
     *
     * @param response
     * @param fileName
     */
    private void setResponseToTransferFile(HttpServletResponse response, String fileName) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ";");
    }

    /**
     * HttpServletRequest를 이용해 SearchCondition 객체를 만들어 리턴한다.
     *
     * @param request
     * @return
     */
    private SearchCondition getSearchCondition(HttpServletRequest request) {
        RequestUtil requestUtil = new RequestUtil(request);
        return SearchCondition.builder()
                .startDate(requestUtil.getStartDate())
                .endDate(requestUtil.getEndDate())
                .category(requestUtil.getCategory())
                .search(requestUtil.getSearch())
                .offset(requestUtil.getOffset())
                .limit(10).build();
    }
}

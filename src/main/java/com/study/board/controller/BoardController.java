package com.study.board.controller;

import com.study.board.dto.SearchCondition;
import com.study.board.response.BoardCreateResponse;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
    public void deleteBoard(HttpServletRequest request, HttpServletResponse response, @RequestParam("i") Long boardId, @RequestParam String password) throws NoSuchAlgorithmException, IOException {
        boardService.deleteBoard(boardId, password);
        String path = generatePath(request, "/");
        response.sendRedirect(path);
    }

    /**
     * 게시글 생성 페이지 접속 시 실행된다.
     * 만일 게시글 생성 유효성 검사에 실패 시 boardFailedCreationDTO를 생성해 기존에 입력한 값을 그대로 돌려준다.
     *
     * @param model
     * @param boardFailedCreationDTO
     * @return
     */
    @GetMapping("/create")
    public String createBoardPage(Model model, BoardFailedCreationDTO boardFailedCreationDTO) {
        BoardCreateResponse boardCreateResponse = boardService.getBoardCreateResponse(boardFailedCreationDTO);
        model.addAttribute("boardResponse", boardCreateResponse);
        return "create";
    }

    /**
     * BoardCreationDTO를 받아 게시글을 DB에, MultipartFile List를 받아 DB에 저장한 후 해당 게시글로 리다이렉트한다.
     *
     * @param response
     * @param files
     * @param boardCreationDTO
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @PostMapping("/create")
    public void createBoard(HttpServletResponse response, @RequestParam("file") List<MultipartFile> files, BoardCreationDTO boardCreationDTO) throws NoSuchAlgorithmException, IOException {
        Long boardId = boardService.createBoard(boardCreationDTO, files);
        String path = getDetailPath(boardId);
        response.sendRedirect(path);
    }

    /**
     * 게시글을 생성할 경우 해당 게시글로 리다이렉트하기 위한 경로를 생성하는 메소드
     * 검색조건을 붙이지 않는다.
     *
     * @param boardId
     * @return
     */
    private String getDetailPath(Long boardId) {
        return "/detail?&i=" + boardId;
    }

    /**
     * 경로를 파라미터로 받아 검색조건을 쿼리 스트링으로 붙인 후 String 값으로 반환한다.
     *
     * @param request
     * @param path
     * @return
     */
    private String generatePath(HttpServletRequest request, String path) {
        RequestUtil requestUtil = new RequestUtil(request);
        return requestUtil.assembleParameter(path);
    }

    /**
     * 파일전송을 시작한다.
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

package com.study.board.service;

import com.study.board.SHA256Encoder;
import com.study.board.domain.Board;
import com.study.board.domain.Category;
import com.study.board.domain.Comment;
import com.study.board.domain.File;
import com.study.board.dto.CommentDTO;
import com.study.board.dto.FileSaveDTO;
import com.study.board.dto.SearchCondition;
import com.study.board.mapper.BoardMapper;
import com.study.board.mapper.CommentMapper;
import com.study.board.mapper.FileMapper;
import com.study.board.response.BoardCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.study.board.controller.BoardControllerAdvice.*;
import static com.study.board.domain.Board.*;
import static com.study.board.dto.BoardDTO.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    //    파일 저장 경로
    @Value("${file.dir}")
    private String PATH;

    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;
    private final ModelMapper modelMapper;
    private final CommentMapper commentMapper;
    private final SHA256Encoder sha256Encoder;

    /**
     * 검색조건을 이용해 BoardListDTO를 만들어 리턴한다.
     *
     * @param searchCondition
     * @return
     */
    public BoardListDTO getBoardListDTO(SearchCondition searchCondition) {
        List<Board> boards = boardMapper.findBoardByPaging(searchCondition);
        List<Category> categories = boardMapper.findAllCategory();
        Integer boardCounts = boardMapper.countByPaging(searchCondition);

        List<BoardSmallDTO> boardSmallDTOs = transformBoardListIntoBoardSmallList(boards, categories);
        setHasFileToBoardSmallDTOs(boards, boardSmallDTOs);
        return new BoardListDTO(boardSmallDTOs, getCategoryNameList(categories), boardCounts);
    }

    /**
     * boardId를 이용해 Board 객체를 DB에서 받아 BoardDetailDTO 객체를 생성해 리턴한다.
     *
     * @param boardId
     * @return
     */
    public BoardDetailDTO getBoardDetailDTO(Long boardId) {
        Board board = boardMapper.findBoardByBoardId(boardId);
        BoardDetailDTO boardDetailDTO = transformBoardIntoBoardDetailDTO(board);

        List<Comment> comments = commentMapper.findCommentsByBoardId(boardId);
        List<File> files = fileMapper.findFilesByBoardId(boardId);
        Category category = boardMapper.findCategoryByCategoryId(board.getCategoryId());
        boardDetailDTO.assembleCategoryAndCommentsAndFileNames(category.getName(), transformCommentIntoCommentDetailDTO(comments), extractFileNamesFromFiles(files));
        return boardDetailDTO;
    }

    /**
     * Board의 views를 1 올린다.
     *
     * @param boardId
     */
    public void updateViews(Long boardId) {
        boardMapper.plusOneViews(boardId);
    }

    /**
     * DB에 있는 비밀번호와 비교 후 같으면 해당 Board를 DB에서 제거하고, 같지 않으면 IllegalArgumentException을 던진다.
     *
     * @param boardId
     * @param password
     * @throws NoSuchAlgorithmException
     */
    public void deleteBoard(Long boardId, String password) throws NoSuchAlgorithmException {
        String encryptedPassword = sha256Encoder.encrypt(password);
        Board board = boardMapper.findBoardByBoardId(boardId);
        if (!encryptedPassword.equals(board.getPassword())) throw new IllegalArgumentException(PASSWORD_ERROR_DELETE);
        boardMapper.deleteBoardByBoardId(boardId);
    }

    /**
     * BoardFailedCreationDTO 객체를 이용해 BoardCreateResponse 객체를 생성해 리턴한다.
     *
     * @param boardFailedCreationDTO
     * @return
     */
    public BoardCreateResponse getBoardCreateResponse(BoardFailedCreationDTO boardFailedCreationDTO) {
        List<String> categoryNames = getCategoryNameList(boardMapper.findAllCategory());
        return new BoardCreateResponse(boardFailedCreationDTO, categoryNames);
    }

    /**
     * boardCreationDTO 객체의 유효성 검사를 한 후 DB에 저장한다.
     * 그 후 파일을 로컬과 DB에 저장한다.
     * 저장된 Board의 Primary Key를 리턴한다.
     *
     * @param boardCreationDTO
     * @param files
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Long createBoard(BoardCreationDTO boardCreationDTO, List<MultipartFile> files) throws NoSuchAlgorithmException, IOException {
        validateBoardCreationDTO(boardCreationDTO);
        BoardSaveDTO boardSaveDTO = saveBoard(boardCreationDTO);
        fileSave(files, boardSaveDTO);
        return boardSaveDTO.getBoardId();
    }

    /**
     * Board가 File을 가지고 있으면 BoardSmallDTO의 hasFile 속성을 true로 바꾼다.
     *
     * @param boards
     * @param boardSmallDTOs
     */
    private void setHasFileToBoardSmallDTOs(List<Board> boards, List<BoardSmallDTO> boardSmallDTOs) {
        List<Long> boardIds = getBoardIdList(boards);
        List<Long> boardIdLinkedFile = getFileListByBoardIdList(boardIds);

        while (!boardIdLinkedFile.isEmpty()) {
            for (Long id : boardIdLinkedFile) {
                if (boardHasFile(id, boardSmallDTOs)) {
                    boardIdLinkedFile.remove(id);
                    break;
                }
            }
        }
    }

    /**
     * Board가 파일을 가지고 있으면 board의 hasfile 필드를 true로 바꾸고 true를 리턴한다.
     * 없으면 false를 리턴한다.
     *
     * @param id
     * @param boardSmallDTOs
     * @return
     */
    private boolean boardHasFile(Long id, List<BoardSmallDTO> boardSmallDTOs) {
        for (BoardSmallDTO boardSmallDTO : boardSmallDTOs) {
            if (boardSmallDTO.getBoardId().equals(id)) {
                boardSmallDTO.setHasFile(true);
                return true;
            }
        }
        return false;
    }

    /**
     * BoardCreationDTO의 비밀번호를 SHA256 알고리즘을 이용해 암호화한다.
     * Board를 DB에 저장한다.
     * 그 후 저장된 Board의 Primary Key를 리턴한다.
     *
     * @param boardCreationDTO
     * @return
     * @throws NoSuchAlgorithmException
     */
    private BoardSaveDTO saveBoard(BoardCreationDTO boardCreationDTO) throws NoSuchAlgorithmException {
        Category category = boardMapper.findCategoryByCategoryName(boardCreationDTO.getCategory());

        boardCreationDTO.encryptPassword(sha256Encoder);
        BoardSaveDTO boardSaveDTO = BoardSaveDTO.transformBoardCreationDTOIntoBoardSaveDTO(boardCreationDTO, category.getCategoryId());
        boardMapper.save(boardSaveDTO);
        return boardSaveDTO;
    }

    /**
     * 로컬에 파일을 저장하고 DB에 저장한다.
     *
     * @param files
     * @param boardSaveDTO
     * @throws IOException
     */
    private void fileSave(List<MultipartFile> files, BoardSaveDTO boardSaveDTO) throws IOException {
        for (MultipartFile file : files) {
//            파일 이름이 null인 경우 확인
            if (isFileNameExist(file)) {
                String renamedFile = fileRename(file.getOriginalFilename());
                saveToLocal(file, renamedFile);
                FileSaveDTO fileSaveDTO = new FileSaveDTO(renamedFile, file.getOriginalFilename(), boardSaveDTO.getBoardId());
                fileMapper.save(fileSaveDTO);
            }
        }
    }

    /**
     * 로컬에 파일을 저장한다.
     *
     * @param file
     * @param renamedFile
     * @throws IOException
     */
    private void saveToLocal(MultipartFile file, String renamedFile) throws IOException {
        file.transferTo(new java.io.File(PATH + renamedFile));
    }

    /**
     * 파일이 존재하는지 확인한다.
     *
     * @param file
     * @return
     */
    private boolean isFileNameExist(MultipartFile file) {
        return file.getOriginalFilename() != null;
    }

    /**
     * File의 이름을 바꾸는 메소드
     * UUID + 현재 날짜 + 오리지널 이름 으로 만든 후 리턴한다.
     *
     * @param fileName
     * @return
     */
    private String fileRename(String fileName) {
        String body;
        String ext;
        int dot = fileName.lastIndexOf(".");
        if (dot != -1) {
            body = fileName.substring(0, dot);
            ext = fileName.substring(dot);
        } else {
            body = fileName;
            ext = "";
        }

        UUID uuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        return uuid.toString() + now + "[" + body + "]" + ext;
    }

    /**
     * BoardCreationDTO 객체의 값들의 유효성 검사를 한다.
     *
     * @param boardCreationDTO
     */
    private void validateBoardCreationDTO(BoardCreationDTO boardCreationDTO) {
        validateWriter(boardCreationDTO.getWriter());
        validatePassword(boardCreationDTO.getPassword());
        validateTitle(boardCreationDTO.getTitle());
        validateContent(boardCreationDTO.getContent());
    }

    /**
     * 작성자 값의 유효성 검사를 한다.
     *
     * @param writer
     */
    private void validateWriter(String writer) {
        if (writer == null || (writer.length() < 3 || writer.length() >= 5)) {
            log.error("WRITER VALIDATION FAILED");
            throw new IllegalArgumentException(WRITER_ERROR);
        }
    }

    /**
     * 비밀번호 값의 유효성 검사를 한다.
     *
     * @param password
     */
    private void validatePassword(String password) {
        if (password != null) {
            validatePasswordPattern(password);
        }
    }

    /**
     * 제목 값의 유효성 검사를 한다.
     *
     * @param title
     */
    private void validateTitle(String title) {
        if (title == null || (title.length() < 4 || title.length() >= 100)) {
            log.error("TITLE VALIDATION FAILED");
            throw new IllegalArgumentException(TITLE_ERROR);
        }
    }

    /**
     * 내용 값의 유효성 검사를 한다.
     *
     * @param content
     */
    private void validateContent(String content) {
        if (content == null || (content.length() < 4 || content.length() >= 2000)) {
            log.error("CONTENT VALIDATION FAILED");
            throw new IllegalArgumentException(CONTENT_ERROR);
        }
    }

    /**
     * 비밀번호 값이 영문, 숫자, 특수문자를 포함한 4글자 이상 16글자 미만의 값인지 유효성 검사를 한다.
     *
     * @param password
     */
    private void validatePasswordPattern(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{4,16}$");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.find()) {
            log.error("PASSWORD VALIDATION FAILED");
            throw new IllegalArgumentException(PASSWORD_ERROR);
        }
    }


    /**
     * Board 객체를 이용해 BoardDetailDTO 객체를 생성해 리턴한다.
     *
     * @param board
     * @return
     */
    private BoardDetailDTO transformBoardIntoBoardDetailDTO(Board board) {
        return modelMapper.map(board, BoardDetailDTO.class);
    }

    /**
     * File List에서 RealName만 추출해 List로 리턴한다.
     *
     * @param files
     * @return
     */
    private List<String> extractFileNamesFromFiles(List<File> files) {
        return files
                .stream()
                .map(File::getRealName)
                .collect(Collectors.toList());
    }

    /**
     * Comment List를 CommentDetailDTO List로 만들어 리턴한다.
     *
     * @param comments
     * @return
     */
    private List<CommentDTO.CommentDetailDTO> transformCommentIntoCommentDetailDTO(List<Comment> comments) {
        return comments
                .stream()
                .map(c -> modelMapper.map(c, CommentDTO.CommentDetailDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * CategoryList에서 Name값들을 모아 List로 만들어 리턴한다.
     *
     * @param categories
     * @return
     */
    private List<String> getCategoryNameList(List<Category> categories) {
        return categories.stream().map(Category::getName)
                .collect(Collectors.toList());
    }

    /**
     * BoardList에서 Id값들을 모아 List로 만들어 리턴한다.
     *
     * @param boards
     * @return
     */
    private List<Long> getBoardIdList(List<Board> boards) {
        return boards.stream().map(Board::getBoardId)
                .collect(Collectors.toList());
    }

    /**
     * Board 객체를 이용해 BoardSmallDTO 객체를 생성한다.
     * 그 후 카테고리를 주입한다.
     *
     * @param boards
     * @param categories
     * @return
     */
    private List<BoardSmallDTO> transformBoardListIntoBoardSmallList(List<Board> boards, List<Category> categories) {
        return boards.stream().map(board -> {
            BoardSmallDTO boardSmall = modelMapper.map(board, BoardSmallDTO.class);
            boardSmall.setCategory(findCategoryById(categories, board));
            return boardSmall;
        }).collect(Collectors.toList());
    }

    /**
     * CategoryList에서 CategoryId와 맞는 객체를 찾아 이름을 리턴한다.
     *
     * @param categories
     * @param board
     * @return
     */
    private String findCategoryById(List<Category> categories, Board board) {
        for (Category category : categories) {
            if (category.getCategoryId().equals(board.getCategoryId())) {
                return category.getName();
            }
        }
//        DB에 장애가 있지 않는 이상 null이 리턴되는 경우는 없다.
        return null;
    }

    /**
     * board_id가 담긴 List를 이용해 File 테이블에 존재하는 중복이 없는 board_id List를 리턴한다.
     *
     * @param boardId
     * @return
     */
    private List<Long> getFileListByBoardIdList(List<Long> boardId) {
        return fileMapper.findBoardIdsByBoardIds(boardId);
    }
}

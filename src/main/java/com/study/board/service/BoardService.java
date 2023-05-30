package com.study.board.service;

import com.study.board.domain.Board;
import com.study.board.domain.Category;
import com.study.board.domain.Comment;
import com.study.board.domain.File;
import com.study.board.dto.BoardDTO;
import com.study.board.dto.CommentDTO;
import com.study.board.dto.SearchCondition;
import com.study.board.mapper.BoardMapper;
import com.study.board.mapper.CommentMapper;
import com.study.board.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;
    private final ModelMapper modelMapper;
    private final CommentMapper commentMapper;

//    TODO Clean Code
//    TODO file 유무 확인 로직 추가해야 함
    /**
     * 검색조건을 이용해 BoardListDTO를 만들어 리턴한다.
     * @param searchCondition
     * @return
     */
    public BoardDTO.BoardListDTO getBoardListDTO(SearchCondition searchCondition) {
        List<Board> boards = boardMapper.findBoardByPaging(searchCondition);
        List<Category> categories = boardMapper.findAllCategory();
//        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = translateBoardListToBoardSmallList(boardList, categoryList, getFileListByBoardIdList(getBoardIdList(boardList)));
        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = translateBoardListToBoardSmallList(boards, categories);

        Integer boardCounts = boardMapper.countByPaging(searchCondition);
        return new BoardDTO.BoardListDTO(boardSmallDTOs, getCategoryNameList(categories), boardCounts);
    }

    /**
     * boardId를 이용해 Board, Comment, Category 객체를 DB에서 받아 BoardDetailDTO 객체를 생성해 리턴한다.
     * @param boardId
     * @return
     */
    public BoardDTO.BoardDetailDTO getBoardDetailDTO(Long boardId) {
        Board board = boardMapper.findBoardByBoardId(boardId);
        List<Comment> comments = commentMapper.findCommentsByBoardId(boardId);
        List<File> files = fileMapper.findFilesByBoardId(boardId);
        Category category = boardMapper.findCategoryByCategoryId(board.getCategoryId());

        return assembleBoardDetailDTO(board, comments, category, files);
    }

    /**
     * Board의 views를 1 올린다.
     * @param boardId
     */
    public void updateViews(Long boardId) {
        boardMapper.plusOneViews(boardId);
    }

//    TODO Clean Code
    /**
     * Board, Comment, Category 객체를 이용해 BoardDetailDTO 객체를 생성해 리턴한다.
     * @param board
     * @param comments
     * @param category
     * @return
     */
    private BoardDTO.BoardDetailDTO assembleBoardDetailDTO(Board board, List<Comment> comments, Category category, List<File> files) {
        List<CommentDTO.CommentDetailDTO> commentDetailDTOs = new ArrayList<>();
        comments.forEach(c -> commentDetailDTOs.add(modelMapper.map(c, CommentDTO.CommentDetailDTO.class)));

        List<String> fileNames = new ArrayList<>();
        files.forEach(f -> fileNames.add(f.getRealName()));

        BoardDTO.BoardDetailDTO boardDetailDTO = modelMapper.map(board, BoardDTO.BoardDetailDTO.class);
        boardDetailDTO.setCategory(category.getName());
        boardDetailDTO.setCommentDetailDTOs(commentDetailDTOs);
        boardDetailDTO.setFileNames(fileNames);
        return boardDetailDTO;
    }

    /**
     * CategoryList에서 Name값들을 모아 List로 만들어 리턴한다.
     * @param categories
     * @return
     */
    private List<String> getCategoryNameList(List<Category> categories) {
        List<String> categoryNames = new ArrayList<>();
        categories.forEach(c -> categoryNames.add(c.getName()));
        return categoryNames;
    }

    /**
     * BoardList에서 Id값들을 모아 List로 만들어 리턴한다.
     * @param boards
     * @return
     */
    private List<Long> getBoardIdList(List<Board> boards) {
        List<Long> boardIds = new ArrayList<>();
        boards.forEach(b -> boardIds.add(b.getBoardId()));
        return boardIds;
    }

    /**
     * Board 객체를 이용해 BoardSmallDTO 객체를 생성한다.
     * 그후 카테고리를 주입한다.
     * FileList에 파일이 해당 Board의 파일이 존재한다면 hasFile을 변경한다.
     *
     * @param boards
     * @param categories
     * @param files
     * @return
     */
    private List<BoardDTO.BoardSmallDTO> translateBoardListToBoardSmallList(List<Board> boards, List<Category> categories, List<File> files) {
        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO.BoardSmallDTO boardSmall = modelMapper.map(board, BoardDTO.BoardSmallDTO.class);
            boardSmall.setCategory(findCategoryById(categories, board));
            boardSmall.setHasFile(hasFile(files, board));
            boardSmallDTOs.add(boardSmall);
        }
        return boardSmallDTOs;
    }

    private List<BoardDTO.BoardSmallDTO> translateBoardListToBoardSmallList(List<Board> boards, List<Category> categories) {
        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO.BoardSmallDTO boardSmall = modelMapper.map(board, BoardDTO.BoardSmallDTO.class);
            boardSmall.setCategory(findCategoryById(categories, board));
            boardSmallDTOs.add(boardSmall);
        }
        return boardSmallDTOs;
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
     * FileList에 board_id와 board의 id값이 같다면 true를 리턴한다.
     * @param board
     * @param files
     * @return
     */
    private boolean hasFile(List<File> files, Board board) {
        for (File file : files) {
            if (board.getBoardId().equals(file.getBoardId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * board_id가 담긴 List를 이용해 FileList를 리턴한다.
     *
     * @param boardId
     * @return
     */
    private List<File> getFileListByBoardIdList(List<Long> boardId) {
        return fileMapper.findFilesByBoardIds(boardId);
    }
}

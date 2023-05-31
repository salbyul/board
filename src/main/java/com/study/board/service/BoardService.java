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
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;
    private final ModelMapper modelMapper;
    private final CommentMapper commentMapper;

//    TODO file 유무 확인 로직 추가해야 함
    /**
     * 검색조건을 이용해 BoardListDTO를 만들어 리턴한다.
     * @param searchCondition
     * @return
     */
    public BoardDTO.BoardListDTO getBoardListDTO(SearchCondition searchCondition) {
        List<Board> boards = boardMapper.findBoardByPaging(searchCondition);
        List<Category> categories = boardMapper.findAllCategory();
        Integer boardCounts = boardMapper.countByPaging(searchCondition);

//        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = transformBoardListIntoBoardSmallList(boardList, categoryList, getFileListByBoardIdList(getBoardIdList(boardList)));
        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = transformBoardListIntoBoardSmallList(boards, categories);
        return new BoardDTO.BoardListDTO(boardSmallDTOs, getCategoryNameList(categories), boardCounts);
    }

    /**
     * boardId를 이용해 Board 객체를 DB에서 받아 BoardDetailDTO 객체를 생성해 리턴한다.
     * @param boardId
     * @return
     */
    public BoardDTO.BoardDetailDTO getBoardDetailDTO(Long boardId) {
        Board board = boardMapper.findBoardByBoardId(boardId);
        BoardDTO.BoardDetailDTO boardDetailDTO = transformBoardIntoBoardDetailDTO(board);
//        TODO Clean Code
        addCommentsAndFilesAndCategoryToBoardDetailDTO(board, boardDetailDTO);
        return boardDetailDTO;
    }

    /**
     * Board의 views를 1 올린다.
     * @param boardId
     */
    public void updateViews(Long boardId) {
        boardMapper.plusOneViews(boardId);
    }

    /**
     * Board 객체를 이용해 BoardDetailDTO 객체를 생성해 리턴한다.
     * @param board
     * @return
     */
    private BoardDTO.BoardDetailDTO transformBoardIntoBoardDetailDTO(Board board) {
        return modelMapper.map(board, BoardDTO.BoardDetailDTO.class);
    }

    /**
     * Board 객체를 이용해 BoardDetailDTO 객체의 Comments, FileNames, Category 속성의 값을 주입한다.
     * @param board
     * @param boardDetailDTO
     */
    private void addCommentsAndFilesAndCategoryToBoardDetailDTO(Board board, BoardDTO.BoardDetailDTO boardDetailDTO) {
        List<Comment> comments = commentMapper.findCommentsByBoardId(board.getBoardId());
        List<File> files = fileMapper.findFilesByBoardId(board.getBoardId());
        Category category = boardMapper.findCategoryByCategoryId(board.getCategoryId());

        boardDetailDTO.assembleCategoryAndCommentsAndFileNames(category.getName(), transformCommentIntoCommentDetailDTO(comments), extractFileNamesFromFiles(files));
    }

    /**
     * File List에서 RealName만 추출해 List로 리턴한다.
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
     * @param categories
     * @return
     */
    private List<String> getCategoryNameList(List<Category> categories) {
        return categories.stream().map(Category::getName)
                .collect(Collectors.toList());
    }

    /**
     * BoardList에서 Id값들을 모아 List로 만들어 리턴한다.
     * @param boards
     * @return
     */
    private List<Long> getBoardIdList(List<Board> boards) {
        return boards.stream().map(Board::getBoardId)
                .collect(Collectors.toList());
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
    private List<BoardDTO.BoardSmallDTO> transformBoardListIntoBoardSmallList(List<Board> boards, List<Category> categories, List<File> files) {
        List<BoardDTO.BoardSmallDTO> boardSmallDTOs = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO.BoardSmallDTO boardSmall = modelMapper.map(board, BoardDTO.BoardSmallDTO.class);
            boardSmall.setCategory(findCategoryById(categories, board));
            boardSmall.setHasFile(hasFile(files, board));
            boardSmallDTOs.add(boardSmall);
        }
        return boardSmallDTOs;
    }

    private List<BoardDTO.BoardSmallDTO> transformBoardListIntoBoardSmallList(List<Board> boards, List<Category> categories) {
        return boards.stream().map(board -> {
            BoardDTO.BoardSmallDTO boardSmall = modelMapper.map(board, BoardDTO.BoardSmallDTO.class);
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

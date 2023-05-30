package com.study.board.mapper;

import com.study.board.domain.Board;
import com.study.board.domain.Category;
import com.study.board.dto.SearchCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    /**
     * 리스트 페이지에 표시할
     * SearchDTO를 이용해 Board들을 List로 만들어 리턴한다.
     * @param searchCondition
     * @return
     */
    List<Board> findBoardByPaging(SearchCondition searchCondition);

    /**
     * DB에 있는 모든 카테고리들을 리턴한다.
     * @return
     */
    List<Category> findAllCategory();

    /**
     * SearchDTO 검색조건에 해당하는 글들의 수를 리턴한다.
     * @param searchCondition
     * @return
     */
    Integer countByPaging(SearchCondition searchCondition);

    /**
     * Board의 primary key를 이용해 Board 객체를 리턴한다.
     * @param boardId
     * @return
     */
    Board findBoardByBoardId(Long boardId);

    /**
     * Category의 primary key를 이용해 Category 객체를 리턴한다.
     * @param categoryId
     * @return
     */
    Category findCategoryByCategoryId(Long categoryId);

    /**
     * Board의 primary key를 이용해 Board의 views 의 수를 1 더한다.
     * @param boardId
     */
    void plusOneViews(Long boardId);
}

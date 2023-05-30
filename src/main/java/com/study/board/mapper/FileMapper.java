package com.study.board.mapper;

import com.study.board.domain.File;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

    /**
     * Board의 primary key List를 이용해 File List를 리턴한다.
     * @param boardIds
     * @return
     */
    List<File> findFilesByBoardIds(List<Long> boardIds);

    /**
     * Board의 primary key를 이용해 File List를 리턴한다.
     * @param boardId
     * @return
     */
    List<File> findFilesByBoardId(Long boardId);
}

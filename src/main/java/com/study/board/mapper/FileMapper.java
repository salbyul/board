package com.study.board.mapper;

import com.study.board.domain.File;
import com.study.board.dto.FileSaveDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {

    /**
     * Board의 primary key List를 이용해 File 테이블에 존재하는 중복이 없는 board_id List를 리턴한다.
     *
     * @param boardIds
     * @return
     */
    List<Long> findBoardIdsByBoardIds(List<Long> boardIds);

    /**
     * Board의 primary key를 이용해 File List를 리턴한다.
     *
     * @param boardId
     * @return
     */
    List<File> findFilesByBoardId(Long boardId);

    /**
     * File의 realName을 이용해 File을 찾아 리턴한다.
     *
     * @param realName
     * @return
     */
    File findFileByRealName(String realName);

    /**
     * FileSaveDTO 객체를 받아서 DB에 저장한다.
     *
     * @param fileSaveDTO
     */
    void save(FileSaveDTO fileSaveDTO);
}

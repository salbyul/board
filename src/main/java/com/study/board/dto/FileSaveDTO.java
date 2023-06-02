package com.study.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 파일을 저장하기 위한 값을 담은 객체
 */
@Getter
@AllArgsConstructor
public class FileSaveDTO {

    private String name;
    private String realName;
    private Long boardId;
}

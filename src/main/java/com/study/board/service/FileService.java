package com.study.board.service;

import com.study.board.domain.File;
import com.study.board.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.study.board.controller.BoardControllerAdvice.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    //    파일이 저장된 경로
    @Value("${file.dir}")
    private String PATH;

    private final FileMapper fileMapper;

    /**
     * 매개변수로 들어온 fileName을 이용해 DB에서 찾아 File 객체로 매핑한 후에 물리적인 파일을 byte[] 로 변환해 리턴한다.
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public byte[] getFileByte(String fileName) throws IOException {
        File file = fileMapper.findFileByRealName(fileName);
        if (file == null) throw new NullPointerException(NO_FILE);
        return Files.readAllBytes(getFilePath(file));
    }

    /**
     * 매개변수로 들어온 File의 Path를 리턴한다.
     *
     * @param file
     * @return
     */
    private Path getFilePath(File file) {
        return getFile(file.getName()).toPath();
    }

    /**
     * 파일의 이름을 매개변수로 받아 파일을 File 객체로 생성해 리턴한다.
     *
     * @param fileName
     * @return
     */
    private java.io.File getFile(String fileName) {
        return new java.io.File(PATH + fileName);
    }

}

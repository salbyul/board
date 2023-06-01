package com.study.board.controller;

import com.study.board.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class BoardControllerAdvice {

    public final static String NO_FILE = "no file";
    public final static String PASSWORD_ERROR_MODIFY = "Password ERROR By Modify";
    public final static String PASSWORD_ERROR_DELETE = "Password ERROR By Delete";

    /**
     * NUllPointerException이 발생했을 경우 404 페이지로 이동한다.
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NullPointerException.class)
    public String nullPointerExceptionHandler(NullPointerException e) {
        log.error("ERROR MESSAGE: [{}]", e.getMessage());
        return "404";
    }

    /**
     * IOException이 발생했을 경우 500 페이지로 이동한다.
     *
     * @param e
     * @return
     */
    @ExceptionHandler(IOException.class)
    public String ioExceptionHandler(IOException e) {
//        이유를 예측하기 힘들기 때문에 printStackTrace() 메소드 사용
        e.printStackTrace();
        return "500";
    }

    /**
     * 파라미터가 잘못 올 경우 각 경우에 맞게 처리한다.
     *
     * @param e
     * @param response
     * @return
     * @throws IOException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentExceptionHandler(IllegalArgumentException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (e.getMessage().equals(PASSWORD_ERROR_MODIFY)) {
            redirectWithIndex(request, response, "/detail", "modify");
            return null;
        } else if (e.getMessage().equals(PASSWORD_ERROR_DELETE)) {
            redirectWithIndex(request, response, "/detail", "delete");
            return null;
        }
        return "505";
    }

//    TODO Clean Code
    /**
     * 파라미터로 들어온 errorCode를 포함시켜 해당 주소로 리다이렉트를 한다.
     *
     * @param request
     * @param response
     * @param path
     * @param errorCode
     * @throws IOException
     */
    private void redirect(HttpServletRequest request, HttpServletResponse response, String path, String errorCode) throws IOException {
        RequestUtil requestUtil = new RequestUtil(request);
        String redirectPath = requestUtil.assembleParameter(path);
        response.sendRedirect(addErrorCode(redirectPath, errorCode));
    }

    private void redirectWithIndex(HttpServletRequest request, HttpServletResponse response, String path, String errorCode) throws IOException {
        RequestUtil requestUtil = new RequestUtil(request);
        String redirectPath = requestUtil.assembleParameterWithIndex(path);
        response.sendRedirect(addErrorCode(redirectPath, errorCode));
    }

    private String addErrorCode(String redirectPath, String errorCode) {
        if (errorCode == null) return redirectPath;
        return redirectPath + "&error=" + errorCode;
    }
}

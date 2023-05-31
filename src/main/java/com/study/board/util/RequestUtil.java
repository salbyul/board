package com.study.board.util;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

@RequiredArgsConstructor
public class RequestUtil {

    private final HttpServletRequest request;

    private final String START_DATE = "start_date";
    private final String END_DATE = "end_date";
    private final String CATEGORY = "category";
    private final String SEARCH = "search";
    private final String PAGE = "page";

    private final int OFFSET_DEFAULT_VALUE = 0;
    private final LocalDate START_DATE_DEFAULT_VALUE = LocalDate.now().minusYears(1);
    private final LocalDate END_DATE_DEFAULT_VALUE = LocalDate.now();

    /**
     * HttpServletRequest의 파라미터 중 'page' key값을 찾아내어 value값을 리턴한다.
     * 기본값은 0이다.
     * @return
     */
    public Integer getOffset() {
        try {
            return Integer.parseInt(request.getParameter(PAGE));
        } catch (Exception e) {
//            예외 발생 시 기본 값을 리턴한다.
            return OFFSET_DEFAULT_VALUE;
        }
    }

    /**
     * HttpServletRequest의 파라미터 중 'start_date' key값을 찾아내어 LocalDate 객체로 변환 후 리턴한다.
     * 기본 값은 현재로부터 1년 전의 날짜이다.
     * @return
     */
    public LocalDate getStartDate() {
        try {
            return transformStringIntoLocalDate(request.getParameter(START_DATE));
        } catch (Exception e) {
//            올바르지 않은 형식의 String 값이 들어오면 기본값이 리턴된다.
            return START_DATE_DEFAULT_VALUE;
        }
    }

    /**
     * HttpServletRequest의 파라미터 중 'end' key값을 찾아내어 value값을 리턴한다.
     * 기본 값은 현재 날짜이다.
     * @return
     */
    public LocalDate getEndDate() {
        try {
            return transformStringIntoLocalDate(request.getParameter(END_DATE));
        } catch (Exception e) {
//            올바르지 않은 형식의 String 값이 들어오면 기본값이 리턴된다.
            return END_DATE_DEFAULT_VALUE;
        }
    }

    /**
     * HttpServletRequest의 파라미터 중 'category' key값을 찾아내어 value값을 리턴한다.
     * @return
     */
    public String getCategory() {
        String category = request.getParameter(CATEGORY);
        if (isEmpty(category)) return null;
        return category;
    }

    /**
     * HttpServletRequest의 파라미터 중 'search' key값을 찾아내어 value값을 리턴한다.
     * @return
     */
    public String getSearch() {
        return request.getParameter(SEARCH);
    }

    /**
     * NNNN-NN-NN 형식의 String을 받아 LocalDate객체로 변환 후 리턴한다.
     * @param date
     * @return
     */
    private LocalDate transformStringIntoLocalDate(String date) throws NoSuchElementException, NumberFormatException, DateTimeException {
        StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
        int year = Integer.parseInt(stringTokenizer.nextToken());
        int month = Integer.parseInt(stringTokenizer.nextToken());
        int day = Integer.parseInt(stringTokenizer.nextToken());
        return LocalDate.of(year, month, day);
    }

    /**
     * Category가 null이거나 빈 문자열이면 false를 리턴한다.
     * @param category
     * @return
     */
    private boolean isEmpty(String category) {
        return category == null || category.equals("");
    }
}

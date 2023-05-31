package com.study.board.util;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;

@RequiredArgsConstructor
public class RequestUtil {

    private final HttpServletRequest request;

    private final int OFFSET_DEFAULT_VALUE = 0;

    private final LocalDateTime START_DATE_DEFAULT_VALUE = LocalDateTime.of(2023, 1, 1, 0, 0);

    private final LocalDateTime END_DATE_DEFAULT_VALUE = LocalDateTime.now();

    /**
     * key를 이용해 HttpServletRequest에서 값을 찾아 리턴한다.
     * 만약, 값이 존재하지 않는다면 defaultValue를 리턴한다.
     * @param key
     * @param defaultValue
     * @return
     */
    public String getParameter(String key, String defaultValue) {
        Optional<String> parameter = Optional.ofNullable(request.getParameter(key));
        return parameter.orElse(defaultValue);
    }

    /**
     * HttpServletRequest의 파라미터 중 'page' key값을 찾아내어 value값을 리턴한다.
     * 기본값은 0이다.
     * @return
     */
    public Integer getOffset() {
        Optional<String> page = Optional.ofNullable(request.getParameter("page"));
        return page.map(Integer::parseInt).orElse(OFFSET_DEFAULT_VALUE);
    }

    /**
     * HttpServletRequest의 파라미터 중 'start_date' key값을 찾아내어 value값을 리턴한다.
     * 기본값은 '2023-01-01'이다.
     * @return
     */
    public LocalDateTime getStartDate() {
        Optional<String> startDate = Optional.ofNullable(request.getParameter("start_date"));
        if (startDate.isPresent() && !startDate.get().equals("")) return translateStringToLocalDateTime(startDate.get());
        return START_DATE_DEFAULT_VALUE;
    }

    /**
     * HttpServletRequest의 파라미터 중 'end' key값을 찾아내어 value값을 리턴한다.
     * 기본 값은 현재 날짜이다.
     * @return
     */
    public LocalDateTime getEndDate() {
        Optional<String> endDate = Optional.ofNullable(request.getParameter("end_date"));
        if (endDate.isPresent() && !endDate.get().equals("")) return translateStringToLocalDateTime(endDate.get());
        return END_DATE_DEFAULT_VALUE;
    }

    /**
     * HttpServletRequest의 파라미터 중 'category' key값을 찾아내어 value값을 리턴한다.
     * 기본 값은 null이다.
     * @return
     */
    public String getCategory() {
        Optional<String> category = Optional.ofNullable(request.getParameter("category"));
        if (category.isEmpty() || category.get().equals("")) return null;
        return category.get();
    }

    /**
     * HttpServletRequest의 파라미터 중 'search' key값을 찾아내어 value값을 리턴한다.
     * 기본 값은 null이다.
     * @return
     */
    public String getSearch() {
        Optional<String> search = Optional.ofNullable(request.getParameter("search"));
        if (search.isEmpty() || search.get().equals("")) return null;
        return search.get();
    }

    /**
     * NNNN-NN-NN 형식의 String을 받아 LocalDate객체로 변환 후 리턴한다.
     * 만약 올바르지 않은 형식의 String을 받게 되면 null을 리턴한다.
     * @param date
     * @return
     */
    private LocalDateTime translateStringToLocalDateTime(String date) {
        StringTokenizer stringTokenizer = new StringTokenizer(date, "-");
        int year, month, day;
        LocalDateTime result;

        try {
            year = Integer.parseInt(stringTokenizer.nextToken());
            month = Integer.parseInt(stringTokenizer.nextToken());
            day = Integer.parseInt(stringTokenizer.nextToken());
            result = LocalDateTime.of(year, month, day, 0, 0);
        } catch (NoSuchElementException | NumberFormatException | DateTimeException e) {
//            올바르지 않은 형식의 String을 받은 경우 null 리턴
            return null;
        }

        return result;
    }
}

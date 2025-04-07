package com.reservemate.reserve_mate_backend.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;

public class Utils {

    public static JSONObject stringToJson(String responseBody) {
        //String failMsg = "처리 중 에러가 발생하였습니다. 다시 시도해주세요.";

        JSONParser jsonParser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) jsonParser.parse(responseBody);
            //failMsg = object.get("message").toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return object;
    }

    // 현재 시간 int형으로 가져오기
    public static int getNowTime() {
        return LocalTime.now().getHour();
    }

    // 한달 날짜 가져오기
    public static List<LocalDate> getDateOfMonth(LocalDate date) {
        date = (date != null) ? date : LocalDate.now();
        LocalDate startDate = LocalDate.of(date.getYear(), date.getMonth(), 1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());

        // 첫날부터 마지막 날짜까지 stream생성하여 List로 변환
        return Stream.iterate(startDate, monthDate -> monthDate.plusDays(1))
            .limit(startDate.until(endDate).getDays() + 1) // 마지막날까지 포함
            .toList();
    }

    // 시간 겹치는지 확인
    public static boolean isTimeConflict(LocalTime existStarTime, LocalTime existEndTime, LocalTime newStarTime,
        LocalTime newEndTime) {
        if (existEndTime == newStarTime)
            return false;
        else if (existStarTime == newEndTime)
            return false;
        else {
            return !(newEndTime.isBefore(existStarTime) || newStarTime.isAfter(existEndTime));
        }
    }

    public static LocalDateTime localDateTimeFormat(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datePattern = localDateTime.format(formatter);
        return LocalDateTime.parse(datePattern);
    }

    public static String localDateFormatWeek(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String datePattern = localDate.format(formatter)
            + " " + localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        return datePattern;
    }

    // 오늘 요일 구하기
    public static DayOfWeek getDayOfWeek() {
        LocalDate toDay = LocalDate.now();
        return toDay.getDayOfWeek();
    }

}

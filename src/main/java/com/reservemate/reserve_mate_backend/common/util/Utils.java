package com.reservemate.reserve_mate_backend.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Utils {

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일");
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

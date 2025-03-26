package com.reservemate.reserve_mate_backend.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Utils {

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

}

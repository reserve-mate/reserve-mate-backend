package com.reservemate.reserve_mate_backend.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static LocalDateTime localDateTimeFormat(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String datePattern = localDateTime.format(formatter);
        return LocalDateTime.parse(datePattern);
    }

}

package com.orion.notepro.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by dgois on 2018-04-21.
 */

public class DateUtil {

    public static final String DEFAULT_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static String dateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
        return dateTime.format(formatter);
    }

    public static String dateTimeToString(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    public static LocalDateTime stringToDateTime(String dateTimeAsString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
        return LocalDateTime.parse(dateTimeAsString, formatter);
    }

    public static LocalDateTime stringToDateTime(String dateTimeAsString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateTimeAsString, formatter);
    }
}

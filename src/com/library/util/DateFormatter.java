package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMATTER);
    }
}


package com.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IDGenerator {
    private static final DateTimeFormatter DATE = DateTimeFormatter.BASIC_ISO_DATE;

    public String newId() {
        return UUID.randomUUID().toString();
    }

    public String newRecordId() {
        return "REC-" + LocalDate.now().format(DATE) + "-" + shortId();
    }

    public String newReservationId() {
        return "RES-" + LocalDate.now().format(DATE) + "-" + shortId();
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

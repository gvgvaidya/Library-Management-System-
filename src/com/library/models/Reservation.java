package com.library.models;

import com.library.enums.ReservationStatus;
import java.time.LocalDate;

public class Reservation {
    private final String reservationId;
    private final String patronId;
    private final String bookIsbn;
    private final LocalDate reservationDate;
    private ReservationStatus status;

    public Reservation(String reservationId, String patronId, String bookIsbn, LocalDate reservationDate, ReservationStatus status) {
        this.reservationId = reservationId;
        this.patronId = patronId;
        this.bookIsbn = bookIsbn;
        this.reservationDate = reservationDate;
        this.status = status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}

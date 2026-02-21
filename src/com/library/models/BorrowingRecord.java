package com.library.models;

import java.time.LocalDate;

public class BorrowingRecord {
    private final String recordId;
    private final String patronId;
    private final String bookIsbn;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowingRecord(String recordId, String patronId, String bookIsbn, LocalDate checkoutDate, LocalDate dueDate) {
        this.recordId = recordId;
        this.patronId = patronId;
        this.bookIsbn = bookIsbn;
        this.checkoutDate = checkoutDate;
        this.dueDate = dueDate;
    }

    public String getRecordId() {
        return recordId;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue(LocalDate asOfDate) {
        LocalDate effective = returnDate == null ? asOfDate : returnDate;
        return effective.isAfter(dueDate);
    }
}

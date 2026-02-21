package com.library.models;

import com.library.enums.BookStatus;
import com.library.enums.Genre;

public class Book {
    private final String isbn;
    private String title;
    private String author;
    private int year;
    private Genre genre;
    private BookStatus status;
    private String borrowerId;

    public Book(String isbn, String title, String author, int year, Genre genre, BookStatus status, String borrowerId) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.status = status;
        this.borrowerId = borrowerId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }
}

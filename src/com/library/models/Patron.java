package com.library.models;

public class Patron {
    private final String patronId;
    private String name;
    private String email;
    private String phone;
    private boolean active;
    private int borrowedBooksCount;

    public Patron(String patronId, String name, String email, String phone, boolean active) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.borrowedBooksCount = 0;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getBorrowedBooksCount() {
        return borrowedBooksCount;
    }

    public void incrementBorrowed() {
        borrowedBooksCount += 1;
    }

    public void decrementBorrowed() {
        if (borrowedBooksCount > 0) {
            borrowedBooksCount -= 1;
        }
    }
}

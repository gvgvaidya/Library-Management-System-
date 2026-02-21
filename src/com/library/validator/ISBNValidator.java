package com.library.validator;

public class ISBNValidator {
    public boolean isValid(String isbn) {
        if (isbn == null) {
            return false;
        }
        String digits = isbn.replaceAll("-", "");
        return digits.matches("\\d{10}|\\d{13}");
    }
}


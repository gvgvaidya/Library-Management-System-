package com.library.validator;

public class PhoneValidator {
    public boolean isValid(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^[0-9+() -]{7,20}$");
    }
}


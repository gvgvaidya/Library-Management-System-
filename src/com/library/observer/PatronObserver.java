package com.library.observer;

import com.library.enums.NotificationType;
import com.library.util.Logger;

public class PatronObserver implements Observer {
    private final String patronId;

    public PatronObserver(String patronId) {
        this.patronId = patronId;
    }

    @Override
    public void update(NotificationType type, String bookIsbn, String message) {
        Logger.info("Notify patron " + patronId + " about " + bookIsbn + " => " + type + ": " + message);
    }
}

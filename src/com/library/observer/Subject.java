package com.library.observer;

import com.library.enums.NotificationType;

public interface Subject {
    void register(String bookIsbn, Observer observer);
    void unregister(String bookIsbn, Observer observer);
    void notifyObservers(NotificationType type, String bookIsbn, String message);
}

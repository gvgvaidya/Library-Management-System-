package com.library.observer;

import com.library.enums.NotificationType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationNotifier implements Subject {
    private final Map<String, List<Observer>> observersByBook = new HashMap<>();

    @Override
    public void register(String bookIsbn, Observer observer) {
        observersByBook.computeIfAbsent(bookIsbn, key -> new ArrayList<>()).add(observer);
    }

    @Override
    public void unregister(String bookIsbn, Observer observer) {
        List<Observer> observers = observersByBook.get(bookIsbn);
        if (observers != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(NotificationType type, String bookIsbn, String message) {
        List<Observer> observers = observersByBook.get(bookIsbn);
        if (observers == null) {
            return;
        }
        for (Observer observer : new ArrayList<>(observers)) {
            observer.update(type, bookIsbn, message);
        }
    }
}

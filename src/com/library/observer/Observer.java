package com.library.observer;

import com.library.enums.NotificationType;

public interface Observer {
    void update(NotificationType type, String bookIsbn, String message);
}

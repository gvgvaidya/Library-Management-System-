package com.library.repositories;

import com.library.models.Reservation;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationRepository {
    private final Map<String, Deque<Reservation>> queues = new HashMap<>();

    public void enqueue(Reservation reservation) {
        queues.computeIfAbsent(reservation.getBookIsbn(), key -> new ArrayDeque<>()).addLast(reservation);
    }

    public Reservation dequeue(String isbn) {
        Deque<Reservation> queue = queues.get(isbn);
        if (queue == null || queue.isEmpty()) {
            return null;
        }
        return queue.removeFirst();
    }

    public List<Reservation> findAll() {
        List<Reservation> all = new ArrayList<>();
        for (Deque<Reservation> queue : queues.values()) {
            all.addAll(queue);
        }
        return all;
    }

    public List<Reservation> findByBookIsbn(String isbn) {
        Deque<Reservation> queue = queues.get(isbn);
        if (queue == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(queue);
    }

    public void removeReservation(String reservationId) {
        for (Deque<Reservation> queue : queues.values()) {
            queue.removeIf(reservation -> reservation.getReservationId().equalsIgnoreCase(reservationId));
        }
    }

    public boolean hasReservations(String isbn) {
        Deque<Reservation> queue = queues.get(isbn);
        return queue != null && !queue.isEmpty();
    }
}

package com.library.services;

import com.library.constants.ErrorMessages;
import com.library.enums.BookStatus;
import com.library.enums.NotificationType;
import com.library.enums.ReservationStatus;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.models.Book;
import com.library.models.Reservation;
import com.library.observer.PatronObserver;
import com.library.observer.ReservationNotifier;
import com.library.repositories.BookRepository;
import com.library.repositories.ReservationRepository;
import com.library.util.IDGenerator;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final ReservationNotifier notifier;
    private final IDGenerator idGenerator = new IDGenerator();

    public ReservationService(
        ReservationRepository reservationRepository,
        BookRepository bookRepository,
        ReservationNotifier notifier
    ) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.notifier = notifier;
    }

    public Reservation reserve(String patronId, String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND);
        }
        if (book.getStatus() == BookStatus.AVAILABLE) {
            throw new BookNotAvailableException("Book is available; no reservation required.");
        }
        Reservation reservation = new Reservation(
            idGenerator.newReservationId(),
            patronId,
            isbn,
            LocalDate.now(),
            ReservationStatus.PENDING
        );
        reservationRepository.enqueue(reservation);
        notifier.register(isbn, new PatronObserver(patronId));
        notifier.notifyObservers(
            NotificationType.RESERVATION_CREATED,
            isbn,
            "Reservation placed for ISBN " + isbn + " (" + reservation.getReservationId() + ")"
        );
        return reservation;
    }

    public void cancel(String reservationId) {
        reservationRepository.removeReservation(reservationId);
    }

    public List<Reservation> listByBook(String isbn) {
        return reservationRepository.findByBookIsbn(isbn);
    }

    public void notifyIfReserved(String isbn) {
        if (!reservationRepository.hasReservations(isbn)) {
            return;
        }
        Reservation next = reservationRepository.dequeue(isbn);
        if (next == null) {
            return;
        }
        next.setStatus(ReservationStatus.FULFILLED);
        notifier.notifyObservers(
            NotificationType.RESERVATION_AVAILABLE,
            isbn,
            "The book you reserved is now available. Reservation: " + next.getReservationId()
        );
    }
}

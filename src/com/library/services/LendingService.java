package com.library.services;

import com.library.config.ConfigurationManager;
import com.library.constants.AppConstants;
import com.library.constants.ErrorMessages;
import com.library.enums.BookStatus;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BorrowLimitExceededException;
import com.library.exception.InvalidPatronException;
import com.library.models.Book;
import com.library.models.BorrowingRecord;
import com.library.models.Patron;
import com.library.repositories.BorrowingRecordRepository;
import com.library.util.IDGenerator;
import com.library.util.Logger;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LendingService {
    private final BookService bookService;
    private final PatronService patronService;
    private final BorrowingRecordRepository recordRepository;
    private final ReservationService reservationService;
    private final IDGenerator idGenerator = new IDGenerator();
    private final ConfigurationManager config = ConfigurationManager.getInstance();

    public LendingService(
        BookService bookService,
        PatronService patronService,
        BorrowingRecordRepository recordRepository,
        ReservationService reservationService
    ) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.recordRepository = recordRepository;
        this.reservationService = reservationService;
    }

    public BorrowingRecord checkout(String patronId, String isbn) {
        Patron patron = patronService.getPatron(patronId);
        if (!patron.isActive()) {
            throw new InvalidPatronException(ErrorMessages.INVALID_PATRON);
        }
        int maxBooks = config.getInt(AppConstants.CONFIG_MAX_BOOKS, AppConstants.DEFAULT_BORROW_LIMIT);
        if (patron.getBorrowedBooksCount() >= maxBooks) {
            throw new BorrowLimitExceededException(ErrorMessages.BORROW_LIMIT_EXCEEDED);
        }
        Book book = bookService.getBook(isbn);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(ErrorMessages.BOOK_NOT_AVAILABLE);
        }
        book.setStatus(BookStatus.BORROWED);
        book.setBorrowerId(patronId);
        patron.incrementBorrowed();

        int loanDays = config.getInt(AppConstants.CONFIG_LOAN_DAYS, AppConstants.DEFAULT_LOAN_DAYS);
        BorrowingRecord record = new BorrowingRecord(
            idGenerator.newRecordId(),
            patronId,
            isbn,
            LocalDate.now(),
            LocalDate.now().plusDays(loanDays)
        );
        recordRepository.save(record);
        Logger.info("Checkout: " + patronId + " -> " + isbn + " record=" + record.getRecordId());
        return record;
    }

    public double returnBook(String patronId, String isbn) {
        Patron patron = patronService.getPatron(patronId);
        Book book = bookService.getBook(isbn);
        BorrowingRecord record = recordRepository.findActiveByBookIsbn(isbn);
        if (record != null) {
            record.setReturnDate(LocalDate.now());
        }
        book.setStatus(BookStatus.AVAILABLE);
        book.setBorrowerId(null);
        patron.decrementBorrowed();

        double fine = calculateFine(record);
        Logger.info("Return: " + patronId + " -> " + isbn + " fine=" + fine);

        reservationService.notifyIfReserved(isbn);
        return fine;
    }

    public double calculateFine(BorrowingRecord record) {
        if (record == null) {
            return 0.0;
        }
        if (!record.isOverdue(LocalDate.now())) {
            return 0.0;
        }
        long days = ChronoUnit.DAYS.between(record.getDueDate(), record.getReturnDate());
        double rate = config.getDouble(AppConstants.CONFIG_FINE_PER_DAY, AppConstants.DEFAULT_FINE_PER_DAY);
        return Math.max(0.0, days * rate);
    }
}

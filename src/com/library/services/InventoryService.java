package com.library.services;

import com.library.enums.BookStatus;
import com.library.models.Book;
import com.library.models.BorrowingRecord;
import com.library.repositories.BorrowingRecordRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {
    private final BookService bookService;
    private final BorrowingRecordRepository recordRepository;

    public InventoryService(BookService bookService, BorrowingRecordRepository recordRepository) {
        this.bookService = bookService;
        this.recordRepository = recordRepository;
    }

    public void markLost(String isbn) {
        bookService.updateStatus(isbn, BookStatus.LOST);
    }

    public void markAvailable(String isbn) {
        bookService.updateStatus(isbn, BookStatus.AVAILABLE);
    }

    public int totalBooks() {
        return bookService.listBooks().size();
    }

    public long availableBooks() {
        return bookService.listBooks().stream().filter(Book::isAvailable).count();
    }

    public long borrowedBooks() {
        return bookService.listBooks().stream().filter(book -> book.getStatus() == BookStatus.BORROWED).count();
    }

    public List<BorrowingRecord> overdueRecords() {
        List<BorrowingRecord> overdue = new ArrayList<>();
        for (BorrowingRecord record : recordRepository.findActive()) {
            if (record.isOverdue(LocalDate.now())) {
                overdue.add(record);
            }
        }
        return overdue;
    }

    public List<String> mostBorrowedIsbns(int limit) {
        Map<String, Integer> counts = new HashMap<>();
        for (BorrowingRecord record : recordRepository.findAll()) {
            counts.put(record.getBookIsbn(), counts.getOrDefault(record.getBookIsbn(), 0) + 1);
        }
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, entries.size()); i++) {
            result.add(entries.get(i).getKey());
        }
        return result;
    }
}

package com.library.services;

import com.library.enums.Genre;
import com.library.models.Book;
import com.library.models.BorrowingRecord;
import com.library.repositories.BorrowingRecordRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationService {
    private final BorrowingRecordRepository recordRepository;
    private final BookService bookService;

    public RecommendationService(BorrowingRecordRepository recordRepository, BookService bookService) {
        this.recordRepository = recordRepository;
        this.bookService = bookService;
    }

    public List<Book> recommendForPatron(String patronId) {
        Set<String> borrowedIsbns = new HashSet<>();
        Set<Genre> genres = new HashSet<>();
        for (BorrowingRecord record : recordRepository.findByPatron(patronId)) {
            borrowedIsbns.add(record.getBookIsbn());
            Book book = bookService.getBook(record.getBookIsbn());
            genres.add(book.getGenre());
        }

        List<Book> recommendations = new ArrayList<>();
        for (Book book : bookService.listBooks()) {
            if (borrowedIsbns.contains(book.getIsbn())) {
                continue;
            }
            if (genres.contains(book.getGenre())) {
                recommendations.add(book);
            }
        }
        return recommendations;
    }
}

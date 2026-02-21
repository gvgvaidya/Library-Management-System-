package com.library.services;

import com.library.constants.ErrorMessages;
import com.library.enums.BookStatus;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateRecordException;
import com.library.exception.InvalidISBNException;
import com.library.models.Book;
import com.library.repositories.BookRepository;
import com.library.validator.ISBNValidator;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;
    private final ISBNValidator isbnValidator = new ISBNValidator();

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        if (!isbnValidator.isValid(book.getIsbn())) {
            throw new InvalidISBNException(ErrorMessages.INVALID_ISBN);
        }
        if (bookRepository.exists(book.getIsbn())) {
            throw new DuplicateRecordException(ErrorMessages.DUPLICATE_RECORD);
        }
        bookRepository.save(book);
    }

    public Book getBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND);
        }
        return book;
    }

    public void updateBook(Book updated) {
        Book book = getBook(updated.getIsbn());
        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setYear(updated.getYear());
        book.setGenre(updated.getGenre());
        book.setStatus(updated.getStatus());
        book.setBorrowerId(updated.getBorrowerId());
    }

    public void updateStatus(String isbn, BookStatus status) {
        Book book = getBook(isbn);
        book.setStatus(status);
    }

    public void removeBook(String isbn) {
        if (!bookRepository.exists(isbn)) {
            throw new BookNotFoundException(ErrorMessages.BOOK_NOT_FOUND);
        }
        bookRepository.delete(isbn);
    }

    public List<Book> listBooks() {
        return bookRepository.findAll();
    }
}

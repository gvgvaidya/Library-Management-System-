package com.library.repositories;

import com.library.models.Book;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    public void save(Book book) {
        books.put(book.getIsbn(), book);
    }

    public Book findByIsbn(String isbn) {
        return books.get(isbn);
    }

    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    public void delete(String isbn) {
        books.remove(isbn);
    }

    public boolean exists(String isbn) {
        return books.containsKey(isbn);
    }
}

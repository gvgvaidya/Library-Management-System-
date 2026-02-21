package com.library.search;

import com.library.models.Book;
import java.util.List;

public class BookSearchContext {
    private SearchStrategy strategy;

    public BookSearchContext(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Book> search(List<Book> books, String query) {
        return strategy.search(books, query);
    }
}


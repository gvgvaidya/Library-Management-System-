package com.library.search;

import com.library.models.Book;
import java.util.List;

public interface SearchStrategy {
    List<Book> search(List<Book> books, String query);
}


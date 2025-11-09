package com.example.library.good.datastore;

import com.example.library.good.model.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseStore implements DataStore {
    private final Map<String, Book> database = new HashMap<>();

    public DatabaseStore() {
        // sample data
        database.put("111", new Book("111", "Clean Code"));
        database.put("222", new Book("222", "Effective Java"));
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(database.get(isbn));
    }

    @Override
    public void updateBook(Book book) {
        database.put(book.getIsbn(), book);
    }
}

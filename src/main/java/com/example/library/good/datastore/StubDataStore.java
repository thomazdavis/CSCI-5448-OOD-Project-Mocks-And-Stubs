package com.example.library.good.datastore;

import com.example.library.good.model.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StubDataStore implements DataStore {
    private final Map<String, Book> stubStorage = new HashMap<>();

    @Override
    public Optional<Book> findBookByIsbn(String isbn) {
        return Optional.ofNullable(stubStorage.get(isbn));
    }

    @Override
    public void updateBook(Book book) {
        stubStorage.put(book.getIsbn(), book);
    }

    public void addBook(Book book) {
        stubStorage.put(book.getIsbn(), book);
    }
}

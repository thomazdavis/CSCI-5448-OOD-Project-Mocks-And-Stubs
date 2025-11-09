package com.example.library.good.datastore;

import com.example.library.good.model.Book;

import java.util.Optional;

public interface DataStore {
    Optional<Book> findBookByIsbn(String isbn);
    void updateBook(Book book);
}

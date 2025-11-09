package com.example.library.good.service;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;

import java.util.Optional;

public class LibraryService {
    private final DataStore store;

    public LibraryService(DataStore store) {
        this.store = store;
    }

    public boolean issueBook(User user, String isbn) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (!book.isAvailable()) {
                return false; // already issued
            }
            book.setAvailable(false);
            store.updateBook(book);
            return true;
        }
        return false;
    }

    public boolean returnBook(User user, String isbn) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setAvailable(true);
            store.updateBook(book);
            return true;
        }
        return false;
    }
}
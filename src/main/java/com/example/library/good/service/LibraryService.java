package com.example.library.good.service;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;

import java.time.LocalDate;
import java.util.Optional;

public class LibraryService {
    private final DataStore store;
    private final NotificationService notificationService;

    public LibraryService(DataStore store) {
        this.store = store;
        this.notificationService = null; // No notifications
    }

    public LibraryService(DataStore store, NotificationService notificationService) {
        this.store = store;
        this.notificationService = notificationService;
    }

    public boolean issueBook(User user, String isbn) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (!book.isAvailable()) {
                return false;
            }
            book.setAvailable(false);
            store.updateBook(book);
            return true;
        }
        return false;
    }

    public boolean issueBookWithDueDate(User user, String isbn, LocalDate dueDate) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (!book.isAvailable()) {
                return false; // already issued
            }
            book.borrowBy(user, dueDate);
            store.updateBook(book);

            if (notificationService != null) {
                notificationService.notifyBookBorrowed(user, book);
            }
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

    public boolean returnBookEnhanced(User user, String isbn) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.returnBook();
            store.updateBook(book);
            return true;
        }
        return false;
    }

    public void processOverdueBooks(LocalDate currentDate) {
        if (notificationService == null) {
            return;
        }

        // In a real implementation, we'd query all borrowed books
        // For this demo, we just show the pattern
        // This method demonstrates behavior that's perfect for mocking
    }

    public void notifyReservation(User user, String isbn) {
        if (notificationService == null) {
            return;
        }

        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent() && bookOpt.get().isAvailable()) {
            notificationService.notifyReservationAvailable(user, bookOpt.get());
        }
    }
}
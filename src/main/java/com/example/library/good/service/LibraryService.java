package com.example.library.good.service;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Enhanced LibraryService with dependency injection.
 * Now depends on both DataStore and NotificationService,
 * making it highly testable with mocks and stubs.
 */
public class LibraryService {
    private final DataStore store;
    private final NotificationService notificationService;

    /**
     * Constructor with single dependency (backward compatible)
     */
    public LibraryService(DataStore store) {
        this.store = store;
        this.notificationService = null; // No notifications
    }

    /**
     * Constructor with both dependencies (for enhanced functionality)
     */
    public LibraryService(DataStore store, NotificationService notificationService) {
        this.store = store;
        this.notificationService = notificationService;
    }

    /**
     * Original issueBook method (backward compatible)
     */
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

    /**
     * Enhanced issueBook with due date and notifications
     */
    public boolean issueBookWithDueDate(User user, String isbn, LocalDate dueDate) {
        Optional<Book> bookOpt = store.findBookByIsbn(isbn);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            if (!book.isAvailable()) {
                return false; // already issued
            }
            book.borrowBy(user, dueDate);
            store.updateBook(book);

            // Send notification if service is available
            if (notificationService != null) {
                notificationService.notifyBookBorrowed(user, book);
            }
            return true;
        }
        return false;
    }

    /**
     * Original returnBook method (backward compatible)
     */
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

    /**
     * Enhanced returnBook that clears borrower info
     */
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

    /**
     * Checks for overdue books and sends notifications
     */
    public void processOverdueBooks(LocalDate currentDate) {
        if (notificationService == null) {
            return; // Can't notify without service
        }

        // In a real implementation, we'd query all borrowed books
        // For this demo, we just show the pattern
        // This method demonstrates behavior that's perfect for mocking
    }

    /**
     * Notifies user about reserved book availability
     */
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
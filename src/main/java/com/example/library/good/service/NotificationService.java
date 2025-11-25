package com.example.library.good.service;

import com.example.library.good.model.Book;
import com.example.library.good.model.User;

/**
 * Interface for notification services.
 * This demonstrates how OO design with interfaces enables easy mocking.
 */
public interface NotificationService {
    /**
     * Notifies a user that their borrowed book is overdue
     */
    void notifyOverdue(User user, Book book);

    /**
     * Notifies a user that a book they reserved is now available
     */
    void notifyReservationAvailable(User user, Book book);

    /**
     * Sends a confirmation when a book is successfully borrowed
     */
    void notifyBookBorrowed(User user, Book book);
}
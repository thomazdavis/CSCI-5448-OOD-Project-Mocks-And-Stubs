package com.example.library.good.service;

import com.example.library.good.model.Book;
import com.example.library.good.model.User;

public interface NotificationService {
    void notifyOverdue(User user, Book book);

    void notifyReservationAvailable(User user, Book book);

    void notifyBookBorrowed(User user, Book book);
}
package com.example.library.good.service;

import com.example.library.good.model.Book;
import com.example.library.good.model.User;

public class EmailNotificationService implements NotificationService {

    @Override
    public void notifyOverdue(User user, Book book) {
        System.out.println("EMAIL: Dear " + user.getName() +
                ", your book '" + book.getTitle() + "' is overdue!");
    }

    @Override
    public void notifyReservationAvailable(User user, Book book) {
        System.out.println("EMAIL: Dear " + user.getName() +
                ", your reserved book '" + book.getTitle() + "' is now available!");
    }

    @Override
    public void notifyBookBorrowed(User user, Book book) {
        System.out.println("EMAIL: Dear " + user.getName() +
                ", you have successfully borrowed '" + book.getTitle() + "'.");
    }
}
package com.example.library.good.model;

import java.time.LocalDate;

public class Book {
    private String isbn;
    private String title;
    private boolean available;
    private User borrowedBy;
    private LocalDate dueDate;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.available = true;
        this.borrowedBy = null;
        this.dueDate = null;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public User getBorrowedBy() {
        return borrowedBy;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void borrowBy(User user, LocalDate dueDate) {
        this.borrowedBy = user;
        this.dueDate = dueDate;
        this.available = false;
    }

    public void returnBook() {
        this.borrowedBy = null;
        this.dueDate = null;
        this.available = true;
    }

    public boolean isOverdue(LocalDate currentDate) {
        return dueDate != null && currentDate.isAfter(dueDate) && !available;
    }
}
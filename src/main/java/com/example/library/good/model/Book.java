package com.example.library.good.model;

public class Book {
    private String isbn;
    private String title;
    private boolean available;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.available = true;
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
}

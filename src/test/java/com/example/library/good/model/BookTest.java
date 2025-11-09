package com.example.library.good.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {
    @Test
    void testBookProperties() {
        Book book = new Book("123", "Clean Code");
        assertEquals("123", book.getIsbn());
        assertEquals("Clean Code", book.getTitle());
        assertTrue(book.isAvailable());
    }

    @Test
    void testAvailabilitySetter() {
        Book book = new Book("123", "Refactoring");
        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }
}

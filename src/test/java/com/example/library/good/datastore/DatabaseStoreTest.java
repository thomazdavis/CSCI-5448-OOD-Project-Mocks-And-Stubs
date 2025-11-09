package com.example.library.good.datastore;
import com.example.library.good.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


public class DatabaseStoreTest {
    private DatabaseStore databaseStore;

    @BeforeEach
    void setUp() {
        databaseStore = new DatabaseStore();
    }

    @Test
    void testFindBookByIsbn_existingBook() {
        Optional<Book> book = databaseStore.findBookByIsbn("111");
        assertTrue(book.isPresent(), "Book with ISBN 111 should exist");
        assertEquals("Clean Code", book.get().getTitle());
    }

    @Test
    void testFindBookByIsbn_nonExistingBook() {
        Optional<Book> book = databaseStore.findBookByIsbn("999");
        assertTrue(book.isEmpty(), "Book with ISBN 999 should not exist");
    }

    @Test
    void testUpdateBook_newBookAdded() {
        Book newBook = new Book("333", "Design Patterns");
        databaseStore.updateBook(newBook);

        Optional<Book> result = databaseStore.findBookByIsbn("333");
        assertTrue(result.isPresent(), "Newly added book should be retrievable");
        assertEquals("Design Patterns", result.get().getTitle());
    }

    @Test
    void testUpdateBook_existingBookUpdated() {
        Optional<Book> bookOpt = databaseStore.findBookByIsbn("222");
        assertTrue(bookOpt.isPresent());
        Book book = bookOpt.get();
        book.setAvailable(false);

        databaseStore.updateBook(book);

        Optional<Book> updated = databaseStore.findBookByIsbn("222");
        assertTrue(updated.isPresent());
        assertFalse(updated.get().isAvailable(), "Book availability should be updated to false");
    }

    @Test
    void testFindBookByIsbn_caseSensitivity() {
        Optional<Book> book = databaseStore.findBookByIsbn("111");
        assertTrue(book.isPresent());
        Optional<Book> wrongCase = databaseStore.findBookByIsbn("111 ".trim());
        assertTrue(wrongCase.isPresent(), "Trimming input shouldn't affect lookup");
    }
}

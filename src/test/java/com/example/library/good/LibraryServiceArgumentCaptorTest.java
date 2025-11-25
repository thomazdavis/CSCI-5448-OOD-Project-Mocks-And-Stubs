package com.example.library.good;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;
import com.example.library.good.service.LibraryService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Demonstrates ARGUMENT CAPTORS - advanced mocking technique.
 *
 * Argument captors allow you to:
 * 1. Capture arguments passed to mocked methods
 * 2. Perform detailed assertions on those arguments
 * 3. Verify complex object state changes
 *
 * This is especially useful when you need to verify that an object
 * was modified correctly before being passed to a dependency.
 */
public class LibraryServiceArgumentCaptorTest {

    @Test
    void testIssueBook_capturesBookStateChange() {
        // Setup mock
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap001", "Captor Test Book");
        when(mockStore.findBookByIsbn("cap001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "Captor User", false);

        // Execute
        service.issueBook(user, "cap001");

        // Create argument captor for Book class
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

        // Verify updateBook was called and capture the argument
        verify(mockStore).updateBook(bookCaptor.capture());

        // Now we can make detailed assertions on the captured book
        Book capturedBook = bookCaptor.getValue();
        assertFalse(capturedBook.isAvailable(), "Captured book should not be available");
        assertEquals("cap001", capturedBook.getIsbn(), "ISBN should match");
        assertEquals("Captor Test Book", capturedBook.getTitle(), "Title should match");
    }

    @Test
    void testIssueBook_capturesCorrectBookWhenMultipleExist() {
        DataStore mockStore = mock(DataStore.class);

        Book book1 = new Book("cap001", "Book One");
        Book book2 = new Book("cap002", "Book Two");

        when(mockStore.findBookByIsbn("cap001")).thenReturn(Optional.of(book1));
        when(mockStore.findBookByIsbn("cap002")).thenReturn(Optional.of(book2));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Issue both books
        service.issueBook(user, "cap001");
        service.issueBook(user, "cap002");

        // Capture all calls to updateBook
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore, times(2)).updateBook(bookCaptor.capture());

        // Get all captured values
        var capturedBooks = bookCaptor.getAllValues();

        assertEquals(2, capturedBooks.size(), "Should capture both book updates");
        assertEquals("cap001", capturedBooks.get(0).getIsbn());
        assertEquals("cap002", capturedBooks.get(1).getIsbn());

        // Both should be unavailable
        capturedBooks.forEach(book ->
                assertFalse(book.isAvailable(), "All issued books should be unavailable")
        );
    }

    @Test
    void testReturnBook_capturesBookStateRestoration() {
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap003", "Return Test");
        book.setAvailable(false); // Book starts as borrowed

        when(mockStore.findBookByIsbn("cap003")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Return the book
        service.returnBook(user, "cap003");

        // Capture the argument
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore).updateBook(bookCaptor.capture());

        // Verify book is now available
        Book capturedBook = bookCaptor.getValue();
        assertTrue(capturedBook.isAvailable(), "Returned book should be available");
    }

    @Test
    void testIssueBook_verifyNoUpdateWhenBookUnavailable() {
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap004", "Unavailable Book");
        book.setAvailable(false); // Already borrowed

        when(mockStore.findBookByIsbn("cap004")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Try to issue unavailable book
        boolean result = service.issueBook(user, "cap004");

        assertFalse(result, "Should not be able to issue unavailable book");

        // Verify updateBook was NEVER called
        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore, never()).updateBook(bookCaptor.capture());
    }

    @Test
    void testIssueBook_verifyNoUpdateWhenBookNotFound() {
        DataStore mockStore = mock(DataStore.class);
        when(mockStore.findBookByIsbn("nonexistent")).thenReturn(Optional.empty());

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Try to issue non-existent book
        boolean result = service.issueBook(user, "nonexistent");

        assertFalse(result);

        // Verify updateBook was never called
        verify(mockStore, never()).updateBook(any(Book.class));
    }

    @Test
    void testArgumentCaptor_vsDirectAssertion_comparison() {
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap005", "Comparison Test");
        when(mockStore.findBookByIsbn("cap005")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Issue book
        service.issueBook(user, "cap005");

        // Method 1: Direct assertion on original object
        assertFalse(book.isAvailable(), "Direct assertion");

        // Method 2: Capture and assert
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore).updateBook(captor.capture());
        assertFalse(captor.getValue().isAvailable(), "Captured assertion");

        // Both are valid, but captor is useful when:
        // 1. You don't have reference to the original object
        // 2. You need to verify the object's state at the exact moment it was passed
        // 3. Multiple calls are made and you need to check specific ones
    }
}
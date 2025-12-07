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
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap001", "Captor Test Book");
        when(mockStore.findBookByIsbn("cap001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "Captor User", false);

        service.issueBook(user, "cap001");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);

        verify(mockStore).updateBook(bookCaptor.capture());

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

        service.issueBook(user, "cap001");
        service.issueBook(user, "cap002");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore, times(2)).updateBook(bookCaptor.capture());

        var capturedBooks = bookCaptor.getAllValues();

        assertEquals(2, capturedBooks.size(), "Should capture both book updates");
        assertEquals("cap001", capturedBooks.get(0).getIsbn());
        assertEquals("cap002", capturedBooks.get(1).getIsbn());

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

        service.returnBook(user, "cap003");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore).updateBook(bookCaptor.capture());

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

        boolean result = service.issueBook(user, "cap004");

        assertFalse(result, "Should not be able to issue unavailable book");

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore, never()).updateBook(bookCaptor.capture());
    }

    @Test
    void testIssueBook_verifyNoUpdateWhenBookNotFound() {
        DataStore mockStore = mock(DataStore.class);
        when(mockStore.findBookByIsbn("nonexistent")).thenReturn(Optional.empty());

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        boolean result = service.issueBook(user, "nonexistent");

        assertFalse(result);

        verify(mockStore, never()).updateBook(any(Book.class));
    }

    @Test
    void testArgumentCaptor_vsDirectAssertion_comparison() {
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("cap005", "Comparison Test");
        when(mockStore.findBookByIsbn("cap005")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        service.issueBook(user, "cap005");

        assertFalse(book.isAvailable(), "Direct assertion");

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(mockStore).updateBook(captor.capture());
        assertFalse(captor.getValue().isAvailable(), "Captured assertion");
    }
}
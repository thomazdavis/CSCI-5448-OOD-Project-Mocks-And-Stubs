package com.example.library.good;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.datastore.StubDataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;
import com.example.library.good.service.LibraryService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Demonstrates the use of SPIES - a hybrid between mocks and real objects.
 *
 * A spy wraps a real object, allowing:
 * 1. Real method calls to execute (like stubs)
 * 2. Verification of interactions (like mocks)
 *
 * Use spies when you need BOTH:
 * - Real behavior/state
 * - Verification that methods were called
 */
public class LibraryServiceSpyTest {

    @Test
    void testIssueBook_withSpy_verifyAndUseRealBehavior() {
        // Create a real stub store with actual data
        StubDataStore realStore = new StubDataStore();
        Book book = new Book("spy001", "Spy Techniques");
        realStore.addBook(book);

        // Wrap it with a spy - real methods work, but we can verify calls
        DataStore spyStore = spy(realStore);

        LibraryService service = new LibraryService(spyStore);
        User user = new User("u1", "Agent Alice", false);

        // Call the method - it uses REAL implementation
        boolean result = service.issueBook(user, "spy001");

        // Verify success using real state
        assertTrue(result, "Book should be issued successfully");
        assertFalse(book.isAvailable(), "Book should not be available after issuing");

        // Verify interactions (like a mock)
        verify(spyStore).findBookByIsbn("spy001");
        verify(spyStore).updateBook(book);

        // This is the key difference from pure mocks:
        // - Mock: No real behavior, only verification
        // - Spy: Real behavior AND verification
    }

    @Test
    void testReturnBook_withSpy_partialMocking() {
        StubDataStore realStore = new StubDataStore();
        Book book = new Book("spy002", "Advanced Spying");
        book.setAvailable(false); // Book is borrowed
        realStore.addBook(book);

        DataStore spyStore = spy(realStore);
        LibraryService service = new LibraryService(spyStore);
        User user = new User("u2", "Agent Bob", false);

        // Return the book
        boolean result = service.returnBook(user, "spy002");

        // Verify using real state
        assertTrue(result);
        assertTrue(book.isAvailable(), "Book should be available after return");

        // Verify method calls
        verify(spyStore).findBookByIsbn("spy002");
        verify(spyStore).updateBook(book);
    }

    @Test
    void testSpy_vs_Mock_comparison() {
        // MOCK: No real behavior
        DataStore mockStore = mock(DataStore.class);
        Book mockBook = new Book("mock001", "Mocked Book");
        when(mockStore.findBookByIsbn("mock001")).thenReturn(java.util.Optional.of(mockBook));

        LibraryService mockService = new LibraryService(mockStore);
        mockService.issueBook(new User("u1", "User1", false), "mock001");

        // With mock: book.isAvailable() uses REAL Book object behavior
        // But store.findBookByIsbn() returns our stubbed value
        assertFalse(mockBook.isAvailable());

        // SPY: Real behavior
        StubDataStore realStore = new StubDataStore();
        Book spyBook = new Book("spy001", "Spy Book");
        realStore.addBook(spyBook);
        DataStore spyStore = spy(realStore);

        LibraryService spyService = new LibraryService(spyStore);
        spyService.issueBook(new User("u2", "User2", false), "spy001");

        // With spy: EVERYTHING uses real implementation
        // We didn't need to stub anything
        assertFalse(spyBook.isAvailable());

        // Both allow verification
        verify(mockStore).findBookByIsbn("mock001");
        verify(spyStore).findBookByIsbn("spy001");
    }

    @Test
    void testSpy_detectsUnexpectedCalls() {
        StubDataStore realStore = new StubDataStore();
        Book book = new Book("spy003", "Unexpected Calls");
        realStore.addBook(book);

        DataStore spyStore = spy(realStore);
        LibraryService service = new LibraryService(spyStore);

        // Issue the book
        service.issueBook(new User("u1", "User", false), "spy003");

        // Verify expected calls
        verify(spyStore, times(1)).findBookByIsbn("spy003");
        verify(spyStore, times(1)).updateBook(book);

        // Verify no other interactions occurred
        verifyNoMoreInteractions(spyStore);
    }

    @Test
    void testSpy_whenBookNotFound() {
        StubDataStore realStore = new StubDataStore();
        DataStore spyStore = spy(realStore);

        LibraryService service = new LibraryService(spyStore);
        User user = new User("u1", "User", false);

        // Try to issue non-existent book
        boolean result = service.issueBook(user, "nonexistent");

        // Should fail
        assertFalse(result);

        // Verify it tried to find the book
        verify(spyStore).findBookByIsbn("nonexistent");

        // Verify updateBook was never called (book not found)
        verify(spyStore, never()).updateBook(any());
    }
}
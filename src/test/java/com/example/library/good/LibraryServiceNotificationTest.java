package com.example.library.good;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;
import com.example.library.good.service.LibraryService;
import com.example.library.good.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Demonstrates mocking external dependencies like NotificationService.
 *
 * This shows why mocks are essential:
 * 1. We don't want to send real emails during tests
 * 2. We want to verify the service interacts correctly with dependencies
 * 3. Tests remain fast and isolated
 *
 * Without mocking, we would need:
 * - A real email server running
 * - Manual verification of sent emails
 * - Network connectivity
 * - Slow, fragile tests
 */
public class LibraryServiceNotificationTest {

    @Test
    void testIssueBookWithDueDate_sendsNotification() {
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("notify001", "Notification Test");
        when(mockStore.findBookByIsbn("notify001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Alice", false);
        LocalDate dueDate = LocalDate.now().plusDays(14);

        boolean result = service.issueBookWithDueDate(user, "notify001", dueDate);

        assertTrue(result);
        assertFalse(book.isAvailable());
        assertEquals(dueDate, book.getDueDate());

        verify(mockNotifier).notifyBookBorrowed(user, book);

        verify(mockStore).findBookByIsbn("notify001");
        verify(mockStore).updateBook(book);
    }

    @Test
    void testIssueBookWithDueDate_noNotificationWhenBookUnavailable() {
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("notify002", "Unavailable Book");
        book.setAvailable(false);
        when(mockStore.findBookByIsbn("notify002")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Bob", false);

        boolean result = service.issueBookWithDueDate(user, "notify002", LocalDate.now().plusDays(14));

        assertFalse(result);

        verify(mockNotifier, never()).notifyBookBorrowed(any(), any());
        verify(mockStore, never()).updateBook(any());
    }

    @Test
    void testNotifyReservation_sendsNotificationWhenBookAvailable() {
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("notify003", "Reserved Book");
        book.setAvailable(true);
        when(mockStore.findBookByIsbn("notify003")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Charlie", false);

        service.notifyReservation(user, "notify003");

        verify(mockNotifier).notifyReservationAvailable(user, book);
    }

    @Test
    void testNotifyReservation_noNotificationWhenBookUnavailable() {
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("notify004", "Borrowed Book");
        book.setAvailable(false);
        when(mockStore.findBookByIsbn("notify004")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Dana", false);

        service.notifyReservation(user, "notify004");

        verify(mockNotifier, never()).notifyReservationAvailable(any(), any());
    }

    @Test
    void testBackwardCompatibility_serviceWorksWithoutNotifier() {
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("compat001", "Backward Compatible");
        when(mockStore.findBookByIsbn("compat001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        boolean result = service.issueBookWithDueDate(user, "compat001", LocalDate.now().plusDays(7));

        assertTrue(result);
        assertFalse(book.isAvailable());
    }

    @Test
    void testMultipleInteractions_verifyCallOrder() {
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book1 = new Book("order001", "Book One");
        Book book2 = new Book("order002", "Book Two");

        when(mockStore.findBookByIsbn("order001")).thenReturn(Optional.of(book1));
        when(mockStore.findBookByIsbn("order002")).thenReturn(Optional.of(book2));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "User", false);
        LocalDate dueDate = LocalDate.now().plusDays(14);

        service.issueBookWithDueDate(user, "order001", dueDate);
        service.issueBookWithDueDate(user, "order002", dueDate);

        verify(mockNotifier, times(2)).notifyBookBorrowed(eq(user), any(Book.class));

        var inOrder = inOrder(mockNotifier);
        inOrder.verify(mockNotifier).notifyBookBorrowed(user, book1);
        inOrder.verify(mockNotifier).notifyBookBorrowed(user, book2);
    }

    @Test
    void testMocking_preventsRealEmailSending() {

        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("mock001", "Mock Test");
        when(mockStore.findBookByIsbn("mock001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Test User", false);

        service.issueBookWithDueDate(user, "mock001", LocalDate.now().plusDays(14));

        verify(mockNotifier).notifyBookBorrowed(user, book);
    }
}
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
        // Mock both dependencies
        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("notify001", "Notification Test");
        when(mockStore.findBookByIsbn("notify001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Alice", false);
        LocalDate dueDate = LocalDate.now().plusDays(14);

        // Issue book with notification
        boolean result = service.issueBookWithDueDate(user, "notify001", dueDate);

        // Verify book was issued
        assertTrue(result);
        assertFalse(book.isAvailable());
        assertEquals(dueDate, book.getDueDate());

        // CRITICAL: Verify notification was sent
        verify(mockNotifier).notifyBookBorrowed(user, book);

        // Verify store interactions
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

        // Try to issue unavailable book
        boolean result = service.issueBookWithDueDate(user, "notify002", LocalDate.now().plusDays(14));

        // Should fail
        assertFalse(result);

        // CRITICAL: Verify NO notification was sent
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

        // Notify about reservation
        service.notifyReservation(user, "notify003");

        // Verify notification was sent
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

        // Try to notify about unavailable book
        service.notifyReservation(user, "notify004");

        // Should NOT send notification
        verify(mockNotifier, never()).notifyReservationAvailable(any(), any());
    }

    @Test
    void testBackwardCompatibility_serviceWorksWithoutNotifier() {
        // Using old constructor (no notification service)
        DataStore mockStore = mock(DataStore.class);
        Book book = new Book("compat001", "Backward Compatible");
        when(mockStore.findBookByIsbn("compat001")).thenReturn(Optional.of(book));

        // Old-style service without notifications
        LibraryService service = new LibraryService(mockStore);
        User user = new User("u1", "User", false);

        // Should work without throwing NullPointerException
        boolean result = service.issueBookWithDueDate(user, "compat001", LocalDate.now().plusDays(7));

        assertTrue(result);
        assertFalse(book.isAvailable());
        // No notification service, so no notification sent (and that's OK)
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

        // Issue two books
        service.issueBookWithDueDate(user, "order001", dueDate);
        service.issueBookWithDueDate(user, "order002", dueDate);

        // Verify both notifications were sent
        verify(mockNotifier, times(2)).notifyBookBorrowed(eq(user), any(Book.class));

        // Verify in order
        var inOrder = inOrder(mockNotifier);
        inOrder.verify(mockNotifier).notifyBookBorrowed(user, book1);
        inOrder.verify(mockNotifier).notifyBookBorrowed(user, book2);
    }

    @Test
    void testMocking_preventsRealEmailSending() {
        // This test demonstrates the KEY benefit of mocking:
        // We can test notification logic WITHOUT actually sending emails

        DataStore mockStore = mock(DataStore.class);
        NotificationService mockNotifier = mock(NotificationService.class);

        Book book = new Book("mock001", "Mock Test");
        when(mockStore.findBookByIsbn("mock001")).thenReturn(Optional.of(book));

        LibraryService service = new LibraryService(mockStore, mockNotifier);
        User user = new User("u1", "Test User", false);

        service.issueBookWithDueDate(user, "mock001", LocalDate.now().plusDays(14));

        // If we used a REAL EmailNotificationService here:
        // - It would try to connect to an email server
        // - It would actually send an email
        // - Test would be slow and require network
        // - Could fail due to network issues
        // - Would spam real inboxes during testing!

        // With a mock:
        // - No real email sent
        // - Test runs instantly
        // - No external dependencies
        // - We still verify the logic is correct

        verify(mockNotifier).notifyBookBorrowed(user, book);
    }
}
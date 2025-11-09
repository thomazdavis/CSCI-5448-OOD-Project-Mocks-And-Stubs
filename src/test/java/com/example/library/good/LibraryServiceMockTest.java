package com.example.library.good;

import com.example.library.good.datastore.DataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;
import com.example.library.good.service.LibraryService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class LibraryServiceMockTest {
    @Test
    void testIssueBook_usesDataStoreCorrectly() {
        DataStore mockStore = mock(DataStore.class);
        LibraryService lib = new LibraryService(mockStore);
        User user = new User("u1", "Alice", false);
        Book book = new Book("111", "Mocked Book");

        when(mockStore.findBookByIsbn("111")).thenReturn(Optional.of(book));

        lib.issueBook(user, "111");

        verify(mockStore).findBookByIsbn("111");
        verify(mockStore).updateBook(book);
    }

    @Test
    void testReturnBook_updatesBookAvailability() {
        DataStore mockStore = mock(DataStore.class);
        LibraryService lib = new LibraryService(mockStore);
        User user = new User("u2", "Bob", false);
        Book book = new Book("222", "Mockito in Action");
        book.setAvailable(false);

        when(mockStore.findBookByIsbn("222")).thenReturn(Optional.of(book));

        lib.returnBook(user, "222");

        verify(mockStore).findBookByIsbn("222");
        verify(mockStore).updateBook(book);
    }
}

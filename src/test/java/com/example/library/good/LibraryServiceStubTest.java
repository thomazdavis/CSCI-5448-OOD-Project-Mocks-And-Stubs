package com.example.library.good;

import com.example.library.good.datastore.StubDataStore;
import com.example.library.good.model.Book;
import com.example.library.good.model.User;
import com.example.library.good.service.LibraryService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryServiceStubTest {
    @Test
    void testIssueBook_withStubDataStore() {
        StubDataStore stub = new StubDataStore();
        Book b1 = new Book("x1", "Stubbed Book");
        stub.addBook(b1);
        LibraryService lib = new LibraryService(stub);
        User user = new User("u1", "Charlie", false);

        boolean issued = lib.issueBook(user, "x1");

        assertTrue(issued);
        assertFalse(b1.isAvailable());
    }

    @Test
    void testReturnBook_withStubDataStore() {
        StubDataStore stub = new StubDataStore();
        Book b1 = new Book("y1", "Another Stubbed Book");
        b1.setAvailable(false);
        stub.addBook(b1);
        LibraryService lib = new LibraryService(stub);
        User user = new User("u2", "Dana", false);

        boolean returned = lib.returnBook(user, "y1");

        assertTrue(returned);
        assertTrue(b1.isAvailable());
    }
}

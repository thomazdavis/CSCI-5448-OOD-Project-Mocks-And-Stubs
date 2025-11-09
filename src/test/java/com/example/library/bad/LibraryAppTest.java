package com.example.library.bad;


import org.junit.jupiter.api.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryAppTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // Reset static state between tests
        LibraryApp.books.clear();
        LibraryApp.availability.clear();
        LibraryApp.books.add("Book A");
        LibraryApp.books.add("Book B");
        LibraryApp.availability.put("Book A", true);
        LibraryApp.availability.put("Book B", true);

        // Capture console output
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testAddBook() {
        String input = "New Book\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LibraryApp.sc = new Scanner(System.in);
        LibraryApp.addBook();

        assertTrue(LibraryApp.books.contains("New Book"));
        assertTrue(LibraryApp.availability.get("New Book"));
        assertTrue(outputStream.toString().contains("Book added!"));
    }

    @Test
    void testBorrowBookWhenAvailable() {
        String input = "Book A\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LibraryApp.sc = new Scanner(System.in);
        LibraryApp.borrowBook();

        assertFalse(LibraryApp.availability.get("Book A"));
        assertTrue(outputStream.toString().contains("Book borrowed!"));
    }

    @Test
    void testBorrowBookWhenNotAvailable() {
        LibraryApp.availability.put("Book A", false);

        String input = "Book A\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LibraryApp.sc = new Scanner(System.in);
        LibraryApp.borrowBook();

        assertTrue(outputStream.toString().contains("Book not available!"));
    }

    @Test
    void testReturnBook() {
        LibraryApp.availability.put("Book B", false);

        String input = "Book B\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LibraryApp.sc = new Scanner(System.in);
        LibraryApp.returnBook();

        assertTrue(LibraryApp.availability.get("Book B"));
        assertTrue(outputStream.toString().contains("Book returned!"));
    }

    @Test
    void testReturnInvalidBook() {
        String input = "Nonexistent\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        LibraryApp.sc = new Scanner(System.in);
        LibraryApp.returnBook();

        assertTrue(outputStream.toString().contains("Invalid book!"));
    }

    @Test
    void testViewBooks() {
        LibraryApp.viewBooks();
        String output = outputStream.toString();
        assertTrue(output.contains("Book A"));
        assertTrue(output.contains("Book B"));
        assertTrue(output.contains("Available"));
    }
}

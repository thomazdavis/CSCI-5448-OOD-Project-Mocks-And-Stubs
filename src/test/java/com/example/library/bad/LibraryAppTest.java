package com.example.library.bad;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the BAD version of LibraryApp.
 *
 * These tests demonstrate common testing problems with poor OO design:
 * 1. Static state requires manual cleanup between tests
 * 2. I/O dependencies require complex setup (System.in/out redirection)
 * 3. File operations require filesystem cleanup
 * 4. Tests are fragile and tightly coupled to implementation
 * 5. Difficult to isolate behavior
 *
 */
public class LibraryAppTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // PROBLEM 1: Must manually reset static state between tests
        // If we forget this, tests will interfere with each other
        LibraryApp.books.clear();
        LibraryApp.availability.clear();
        LibraryApp.borrowers.clear();

        // Setup test data
        LibraryApp.books.add("Book A");
        LibraryApp.books.add("Book B");
        LibraryApp.availability.put("Book A", true);
        LibraryApp.availability.put("Book B", true);

        // PROBLEM 2: Must redirect System.out to capture output
        // This is fragile and couples tests to console output format
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore original streams
        System.setOut(originalOut);
        System.setIn(originalIn);

        // PROBLEM 3: Must cleanup test files
        try {
            Files.deleteIfExists(Paths.get("library_report.txt"));
        } catch (IOException e) {
            // Cleanup failed
        }
    }

    @Test
    void testAddBook() {
        // PROBLEM 4: Must mock user input using ByteArrayInputStream
        String input = "New Book\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LibraryApp.sc = new Scanner(System.in);

        LibraryApp.addBook();

        // Assertions work, but setup is complicated
        assertTrue(LibraryApp.books.contains("New Book"));
        assertTrue(LibraryApp.availability.get("New Book"));
        assertTrue(outputStream.toString().contains("Book added!"));
    }

    @Test
    void testBorrowBookWhenAvailable() {
        // Complex setup for user input (book name + borrower name)
        String input = "Book A\nAlice\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LibraryApp.sc = new Scanner(System.in);

        LibraryApp.borrowBook();

        assertFalse(LibraryApp.availability.get("Book A"));
        assertEquals("Alice", LibraryApp.borrowers.get("Book A"));
        assertTrue(outputStream.toString().contains("Book borrowed!"));
    }

    @Test
    void testBorrowBookWhenNotAvailable() {
        LibraryApp.availability.put("Book A", false);

        String input = "Book A\nBob\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LibraryApp.sc = new Scanner(System.in);

        LibraryApp.borrowBook();

        // Book remains unavailable
        assertFalse(LibraryApp.availability.get("Book A"));
        assertTrue(outputStream.toString().contains("Book not available!"));
    }

    @Test
    void testReturnBook() {
        LibraryApp.availability.put("Book B", false);
        LibraryApp.borrowers.put("Book B", "Charlie");

        String input = "Book B\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LibraryApp.sc = new Scanner(System.in);

        LibraryApp.returnBook();

        assertTrue(LibraryApp.availability.get("Book B"));
        assertFalse(LibraryApp.borrowers.containsKey("Book B"));
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
        LibraryApp.borrowers.put("Book A", "Dana");
        LibraryApp.availability.put("Book A", false);

        LibraryApp.viewBooks();

        String output = outputStream.toString();
        assertTrue(output.contains("Book A"));
        assertTrue(output.contains("Book B"));
        assertTrue(output.contains("Borrowed by Dana"));
        assertTrue(output.contains("Available"));
    }

    @Test
    void testGenerateReport_createsFile() throws IOException {
        // PROBLEM 5: Test interacts with real filesystem
        // This creates actual files and can fail due to permissions
        LibraryApp.books.add("Test Book");
        LibraryApp.availability.put("Test Book", true);

        LibraryApp.generateReport();

        // Verify file was created
        File reportFile = new File("library_report.txt");
        assertTrue(reportFile.exists(), "Report file should be created");

        // Read and verify contents
        String content = new String(Files.readAllBytes(Paths.get("library_report.txt")));
        assertTrue(content.contains("Library Report"));
        assertTrue(content.contains("Total books: 3")); // 2 from setUp + 1 added

        // PROBLEM 6: Must manually cleanup
        reportFile.delete();
    }

    @Test
    void testGenerateReport_verifyConsoleOutput() {
        LibraryApp.generateReport();

        // PROBLEM 7: Test is brittle - depends on exact console output
        assertTrue(outputStream.toString().contains("Report generated!"));
    }

    @Test
    void testCalculateStatistics() {
        // Set up some borrowed books
        LibraryApp.availability.put("Book A", false);

        LibraryApp.calculateStatistics();

        String output = outputStream.toString();

        // PROBLEM 8: Testing logic by parsing console output
        // If output format changes, test breaks
        assertTrue(output.contains("Total Books: 2"));
        assertTrue(output.contains("Currently Borrowed: 1"));
        assertTrue(output.contains("Borrow Rate: 50.0%"));
    }

    @Test
    void testIsBookAvailable() {
        // This is the EASIEST method to test because it has no I/O
        // But it still accesses global state
        assertTrue(LibraryApp.isBookAvailable("Book A"));

        LibraryApp.availability.put("Book A", false);
        assertFalse(LibraryApp.isBookAvailable("Book A"));

        assertFalse(LibraryApp.isBookAvailable("Nonexistent"));
    }

    @Test
    void testStaticStateInterference() {
        // PROBLEM 9: Tests can interfere with each other if setUp fails
        // Add a book
        LibraryApp.books.add("Interference Test");
        LibraryApp.availability.put("Interference Test", true);

        assertEquals(3, LibraryApp.books.size());

        // If setUp doesn't run before next test, this book persists!
        // This is why static state is dangerous in testing
    }

    @Test
    void testCannotMockDependencies() {
        // PROBLEM 10: Cannot mock the Scanner or FileWriter
        // We're stuck with real implementations

        // This test shows we CAN'T do something like:
        // Scanner mockScanner = mock(Scanner.class);
        // LibraryApp.sc = mockScanner;
        // when(mockScanner.nextLine()).thenReturn("Test Book");

        // Because borrowBook() calls System.out.println and other methods
        // that are tightly coupled to the implementation

        // We must use real ByteArrayInputStream instead
        String input = "Book A\nMockUser\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        LibraryApp.sc = new Scanner(System.in);

        LibraryApp.borrowBook();

        // This works but is fragile and verbose
        assertFalse(LibraryApp.availability.get("Book A"));
    }

    /**
     * This test documents WHY the bad design is hard to test.
     */
//    @Test
//    void testComplexity_comparedToGoodVersion() {
//        // Count the problems in this test class:
//        // 1. Manual static state management (setUp/tearDown)
//        // 2. System.in/out redirection
//        // 3. ByteArrayInputStream for input simulation
//        // 4. File system cleanup
//        // 5. String parsing to verify logic
//        // 6. Cannot use mocks or stubs
//        // 7. Cannot inject test doubles
//        // 8. Tests are slow (I/O operations)
//        // 9. Tests are brittle (depend on exact output format)
//        // 10. Cannot isolate components
//
//        assertTrue(true, "See comments above for why this is problematic");
//    }
}
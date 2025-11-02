package com.example.library.bad;

import org.junit.jupiter.api.Test;

class LibraryServiceTest {
    @Test
    void testIssueBook() {
        LibraryService lib = new LibraryService();
        lib.issueBook("Sam", "My Life in Red and White"); // this calls the real Db
        // Can't mock a db here
    }
}
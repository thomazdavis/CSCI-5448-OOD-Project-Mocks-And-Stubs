package com.example.library.bad;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * BAD DESIGN - Demonstrates anti-patterns that make testing difficult:
 * - Static methods and fields (global state)
 * - Direct I/O dependencies (Scanner, FileWriter)
 * - No dependency injection
 * - Mixed concerns (UI, business logic, persistence)
 * - Tight coupling
 */
public class LibraryApp {
    static ArrayList<String> books = new ArrayList<>();
    static HashMap<String, Boolean> availability = new HashMap<>();
    static HashMap<String, String> borrowers = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    static void addBook() {
        System.out.println("Enter book name:");
        String name = sc.nextLine();
        books.add(name);
        availability.put(name, true);
        System.out.println("Book added!");
    }

    static void borrowBook() {
        System.out.println("Enter book name:");
        String name = sc.nextLine();
        if (availability.containsKey(name) && availability.get(name)) {
            System.out.println("Enter your name:");
            String borrower = sc.nextLine();
            availability.put(name, false);
            borrowers.put(name, borrower);
            System.out.println("Book borrowed!");
        } else {
            System.out.println("Book not available!");
        }
    }

    static void returnBook() {
        System.out.println("Enter book name:");
        String name = sc.nextLine();
        if (availability.containsKey(name)) {
            availability.put(name, true);
            borrowers.remove(name);
            System.out.println("Book returned!");
        } else {
            System.out.println("Invalid book!");
        }
    }

    static void viewBooks() {
        System.out.println("Books in library:");
        for (String book : books) {
            String status = availability.get(book) ? "Available" : "Borrowed";
            if (!availability.get(book) && borrowers.containsKey(book)) {
                status += " by " + borrowers.get(book);
            }
            System.out.println(book + " - " + status);
        }
    }

    /**
     * Anti-pattern: Direct file I/O mixed with business logic
     * This is difficult to test because:
     * 1. Creates real files (side effects)
     * 2. Can't mock FileWriter
     * 3. Requires file system cleanup after tests
     */
    static void generateReport() {
        try {
            FileWriter writer = new FileWriter("library_report.txt");
            writer.write("Library Report\n");
            writer.write("Generated: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + "\n");
            writer.write("Total books: " + books.size() + "\n");

            int available = 0;
            for (Boolean avail : availability.values()) {
                if (avail) available++;
            }

            writer.write("Available: " + available + "\n");
            writer.write("Borrowed: " + (books.size() - available) + "\n");
            writer.close();
            System.out.println("Report generated!");
        } catch (IOException e) {
            System.out.println("Error generating report!");
        }
    }

    /**
     * Anti-pattern: Business logic mixed with printing
     * Can't test the calculation without dealing with System.out
     */
    static void calculateStatistics() {
        int total = books.size();
        int borrowed = 0;
        for (Boolean avail : availability.values()) {
            if (!avail) borrowed++;
        }

        double borrowRate = total > 0 ? (borrowed * 100.0 / total) : 0;

        // Logic and presentation mixed together
        System.out.println("=== Library Statistics ===");
        System.out.println("Total Books: " + total);
        System.out.println("Currently Borrowed: " + borrowed);
        System.out.println("Borrow Rate: " + String.format("%.1f%%", borrowRate));
    }

    /**
     * Anti-pattern: No way to inject test data
     * Tests must modify static state directly
     */
    static boolean isBookAvailable(String bookName) {
        return availability.containsKey(bookName) && availability.get(bookName);
    }
}
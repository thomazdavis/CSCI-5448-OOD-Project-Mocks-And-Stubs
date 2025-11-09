package com.example.library.bad;


import java.util.*;

public class LibraryApp {
    static ArrayList<String> books = new ArrayList<>();
    static HashMap<String, Boolean> availability = new HashMap<>();
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
            availability.put(name, false);
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
            System.out.println("Book returned!");
        } else {
            System.out.println("Invalid book!");
        }
    }

    static void viewBooks() {
        System.out.println("Books in library:");
        for (String book : books) {
            System.out.println(book + " - " + (availability.get(book) ? "Available" : "Borrowed"));
        }
    }
}

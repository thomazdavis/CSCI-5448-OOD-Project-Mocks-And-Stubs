package com.example.library.bad;

public class LibraryService {
    private Database db = new Database();

    public void issueBook(String user, String book) {
        db.updateRecord(user, book);
    }
}

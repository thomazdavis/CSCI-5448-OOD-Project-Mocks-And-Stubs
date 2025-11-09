package com.example.library.good.model;

public class User {
    private String id;
    private String name;
    private boolean admin;

    public User(String id, String name, boolean admin) {
        this.id = id;
        this.name = name;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return admin;
    }
}

package com.example.library.good.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserTest {
    @Test
    void testUserProperties() {
        User user = new User("u1", "Mukund", false);
        assertEquals("u1", user.getId());
        assertEquals("Mukund", user.getName());
        assertFalse(user.isAdmin());
    }
}

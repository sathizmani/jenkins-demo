package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppTest {
    @Test
    public void testGetGreeting() {
        App app = new App();
        assertEquals("Hello, Team! This is a successful Cloud-based CI/CD Pipeline Demo.", app.getGreeting());
    }
}

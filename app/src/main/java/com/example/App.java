package com.example;

public class App {
    public String getGreeting() {
        return "Hello, Team! This is a successful Cloud-based CI/CD Pipeline Demo.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}

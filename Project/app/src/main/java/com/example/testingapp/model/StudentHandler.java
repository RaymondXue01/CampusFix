package com.example.testingapp.model;

public class StudentHandler implements AccountHandler {
    // Attributes
    private String name;

    // Constructors
    public StudentHandler(String name) {
        this.name = name;
    }

    public StudentHandler() {
        this("John Doe");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}

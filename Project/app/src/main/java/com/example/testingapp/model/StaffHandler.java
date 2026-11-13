package com.example.testingapp.model;

public class StaffHandler implements AccountHandler, Staff {
    // Attributes
    private String name;

    // From Staff Interface
    @Override
    public void dummy() {
        // Example of future staff-only feature implementation
    }

    // Constructors
    public StaffHandler(String name) {
        this.name = name;
    }

    public StaffHandler() {
        this("Staff Doe");
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

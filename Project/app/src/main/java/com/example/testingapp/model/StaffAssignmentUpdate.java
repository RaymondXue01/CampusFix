package com.example.testingapp.model;

public class StaffAssignmentUpdate implements UpdateType {
    @Override
    public String getType() {
        return "assignedStaff";
    }
}

package com.example.testingapp.model;

public class StatusUpdate implements UpdateType {
    @Override
    public String getType() {
        return "status";
    }
}

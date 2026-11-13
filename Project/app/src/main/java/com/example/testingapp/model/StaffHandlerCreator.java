package com.example.testingapp.model;

public class StaffHandlerCreator implements AccountHandlerCreator {

    @Override
    public AccountHandler createHandler() {
        return new StaffHandler();
    }
}

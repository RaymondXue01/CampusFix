package com.example.testingapp.model;

public class StudentHandlerCreator implements AccountHandlerCreator {

    @Override
    public AccountHandler createHandler() {
        return new StudentHandler();
    }
}

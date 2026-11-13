package com.example.testingapp.model;


public class AccountModel {
    // Used to keep track of the number of accounts created, as well as type

    // Account specific information
    private final String email1;
    private final int passwordHash;
    private final Boolean isStaff;

    private final AccountHandler handler;
    // Singleton Object
    private static AccountModel account;

    public String getEmail1() {
        return email1;
    }

    public Boolean getIsStaff() {
        return isStaff;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public AccountHandler getHandler() {
        return handler;
    }

    // Logout method
    public static void logout() {
        synchronized (AccountModel.class) {
            account = null;
        }
    }

    // Reset account stats (Testing purposes)
    public static void reset() {
        AccountModel.account = null;
    }


    // Constructor for Singleton Object
    private AccountModel(String email, int password, Boolean isStaff) {
        this.email1 = email;
        this.passwordHash = password;
        this.isStaff = isStaff;
        AccountHandlerCreator creator;
        if (Boolean.TRUE.equals(isStaff)) { // Refactored to avoid potential NPE
            // Create Staff handler
            creator = new StaffHandlerCreator();
        } else {
            // Create Student handler
            creator = new StudentHandlerCreator();
        }
        handler = creator.createHandler();

    }

    // No arg Constructor
    private AccountModel() {
        this("default", "password".hashCode(), false);
    }

    // Get instance of the AccountModel
    // This implementation should be used from CreateAccount view
    public static synchronized AccountModel getInstance(String email,
                                                        int password, Boolean isStaff) {
        account = new AccountModel(email, password, isStaff);
        return account;
    }

    public static synchronized AccountModel getInstance() {
        if (account == null) {
            account = new AccountModel();
        }
        return account;
    }
}

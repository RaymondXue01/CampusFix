package com.example.testingapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.example.testingapp.model.AccountModel;

import java.util.HashMap;

public class CreateAccountTest {
    private HashMap<String, AccountModel> accountMap;
    private static final int TIMEOUT = 200;
    private static final String ACCOUNT_1 = "account1";
    private static final int PASSWORD = "password".hashCode();

    @Before
    public void setup() {
        accountMap = new HashMap<>();
        AccountModel.reset();
    }

    @Test(timeout = TIMEOUT)
    public void initialization() {
        assertEquals(0, accountMap.size());
    }

    @Test(timeout = TIMEOUT)
    public void createAccount() {
        AccountModel account = AccountModel.getInstance(ACCOUNT_1, PASSWORD, false);
        accountMap.put(account.getEmail1(), account);
    }

    @Test(timeout = TIMEOUT)
    public void createAccountStaff() {
        AccountModel account = AccountModel.getInstance(ACCOUNT_1, PASSWORD, true);
        accountMap.put(account.getEmail1(), account);
    }

    @Test(timeout = TIMEOUT)
    public void onlyOneInstance() {
        AccountModel account = AccountModel.getInstance(ACCOUNT_1, PASSWORD, true);
        AccountModel another = AccountModel.getInstance("account2", PASSWORD, false);
        assertEquals(ACCOUNT_1, account.getEmail1());
        assertEquals(ACCOUNT_1, another.getEmail1());
        assertEquals(true, account.getIsStaff());
        assertEquals(true, another.getIsStaff());
    }

}

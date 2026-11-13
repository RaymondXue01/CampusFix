package com.example.testingapp;

import com.example.testingapp.model.AccountHandler;
import com.example.testingapp.model.AccountHandlerCreator;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.model.StaffHandler;
import com.example.testingapp.model.StaffHandlerCreator;
import com.example.testingapp.model.StudentHandler;
import com.example.testingapp.model.StudentHandlerCreator;
import static org.junit.Assert.*;

import org.junit.Test;
public class AccountHandlerFactoryTest {

    @Test
    public void testCreatorReturnType() {
        AccountHandlerCreator studentCreator = new StudentHandlerCreator();
        AccountHandlerCreator staffCreator = new StaffHandlerCreator();

        AccountHandler studentHandler = studentCreator.createHandler();
        AccountHandler staffHandler = staffCreator.createHandler();

        assertTrue(studentHandler instanceof StudentHandler);
        assertTrue(staffHandler instanceof StaffHandler);
    }

    @Test
    public void testCorrectHandlerType() {
        AccountModel.reset();
        AccountModel studentAccount = AccountModel.getInstance("testingStudent", 123, false);
        assertTrue(studentAccount.getHandler() instanceof StudentHandler);

        AccountModel.reset();
        AccountModel staffAccount = AccountModel.getInstance("testingStaff", 123, true);
        assertTrue(staffAccount.getHandler() instanceof StaffHandler);
    }
}

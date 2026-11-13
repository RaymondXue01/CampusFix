package com.example.testingapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testingapp.model.AccountHandler;
import com.example.testingapp.model.AccountModel;

public class ProfileViewModel extends ViewModel {

    // MutableLiveData for observable data
    private MutableLiveData<String> userName;
    private MutableLiveData<String> userEmail;
    private MutableLiveData<String> userRole;
    private MutableLiveData<String> headerTitle;

    // Constructor
    public ProfileViewModel() {
        // Initialize MutableLiveData
        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        userRole = new MutableLiveData<>();
        headerTitle = new MutableLiveData<>();

        // Set placeholder values (Sprint 1)
        AccountModel account = AccountModel.getInstance();
        AccountHandler handler = account.getHandler();
        userName.setValue(handler.getName());
        userEmail.setValue(account.getEmail1());
        if (Boolean.TRUE.equals(account.getIsStaff())) {
            userRole.setValue("Staff");
        } else {
            userRole.setValue("Student");
        }

        headerTitle.setValue("Profile");
    }

    // Getters that return LiveData (not MutableLiveData)
    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public LiveData<String> getUserRole() {
        return userRole;
    }

    public LiveData<String> getHeaderTitle() {
        return headerTitle;
    }

    // Optional: Method to update profile data (will be used in future sprints)
    public void updateProfile(String name, String email, String role) {
        userName.setValue(name);
        userEmail.setValue(email);
        userRole.setValue(role);
    }

    // Logout function
    public void logout() {
        // Clear the current account
        AccountModel.logout();
    }
}
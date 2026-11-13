package com.example.testingapp.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testingapp.model.AccountModel;
import com.example.testingapp.model.Pair;
import com.example.testingapp.model.PairRegistry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginViewModel extends ViewModel {
    private static final String INVALID_INPUT = "Invalid input";

    // Use mutableLiveData to hold the data that can be observed by the view
    private MutableLiveData<String> title;
    private MutableLiveData<String> emailHint;
    private MutableLiveData<String> passHint;
    private MutableLiveData<String> feedback;

    // Set up accountMap
    private HashMap<String, AccountModel> accountMap;

    // Database Reference
    private DatabaseReference databaseReference;

    // Constructor
    public LoginViewModel() {
        title = new MutableLiveData<>();
        title.setValue("Login");
        emailHint = new MutableLiveData<>("Email");
        passHint = new MutableLiveData<>("Password");
        feedback = new MutableLiveData<>();

        // Create a new accountMap
        accountMap = new HashMap<>();

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("accounts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    String temp = snapshot.getValue(String.class);
                    if (temp != null) {

                        // Create account from database string
                        // Split the temp string into an email, passwordHash, and staff status
                        String email;
                        int passwordHash;

                        int emailEndIndex = temp.indexOf(",");
                        email = temp.substring(0, emailEndIndex);
                        String rest = temp.substring(emailEndIndex + 1);

                        int passwordEndIndex = rest.indexOf(",");
                        passwordHash = Integer.parseInt(rest.substring(0, passwordEndIndex));
                        rest = rest.substring(passwordEndIndex + 1);

                        if (rest.equals("true")) {
                            Pair<String, Integer, Boolean> pair = new Pair<>(
                                    email,
                                    passwordHash,
                                    true);
                            PairRegistry.register(email, pair);
                        } else {
                            Pair<String, Integer, Boolean> pair = new Pair<>(
                                    email,
                                    passwordHash,
                                    false);
                            PairRegistry.register(email, pair);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                // Required by interface - leave empty
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Required by interface - leave empty
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // Required by interface - leave empty
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Required by interface - leave empty
            }
        });
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<String> getEmailHint() {
        return emailHint;
    }

    public LiveData<String> getPassHint() {
        return passHint;
    }

    public LiveData<String> getFeedback() {
        return feedback;
    }

    public HashMap<String, AccountModel> getAccountMap() {
        return accountMap;
    }

    public void updateFeedback(Boolean success) {
        if (Boolean.TRUE.equals(success)) {
            feedback.setValue("Successfully logged in!");
        } else {
            feedback.setValue("Unable to login, check your email or password.");
        }
    }

    public boolean attemptLogin(String email, String password) {
        // Create try, catch statements to check for invalid inputs
        try {
            // Check if the email is valid
            // First Check: check that none of the inputs are null
            if ((email == null) || (password == null)) {
                throw new IllegalArgumentException(INVALID_INPUT);
            }

            // Second Check: ensure that the email and password are not full of whitespace or empty
            if ((email.isBlank()) || (email.isEmpty()) || (password.isBlank())
                    || (password.isEmpty())) {
                throw new IllegalArgumentException(INVALID_INPUT);
            }

            // Third Check: ensure the email and password are not duplicates
            if (email.equals(password)) {
                throw new IllegalArgumentException(INVALID_INPUT);
            }

            // Additional Check: ensure that email does not contain a comma
            if (email.contains(",")) {
                throw new IllegalArgumentException(INVALID_INPUT);
            }

            // Check if the email is in the accountMap
            if (PairRegistry.contains(email)) {
                // Retrieve the account associated
                Pair account = PairRegistry.get(email);

                // Check if the provided password matches the account
                if (account.getVal1().equals(password.hashCode())) {
                    // Successful login
                    updateFeedback(true);
                    AccountModel.reset();
                    AccountModel.getInstance(
                            (String) account.getVal0(),
                            (Integer) account.getVal1(),
                            (Boolean) account.getVal2());
                } else {
                    throw new IllegalArgumentException(INVALID_INPUT);
                }
            } else {
                throw new IllegalArgumentException(INVALID_INPUT);
            }

            // Assuming no errors, launch Issue Feed
            return true;
        } catch (Exception e) {
            updateFeedback(false);
            return false;
        }
    }
}

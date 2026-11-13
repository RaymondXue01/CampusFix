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
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class CreateAccountViewModel extends ViewModel {
    private static final String INVALID_INPUT = "Invalid input";
    // Use MutableLiveData to hold the data that can be observed by the view
    private MutableLiveData<String> title;
    private MutableLiveData<String> emailHint;
    private MutableLiveData<String> passHint;
    private MutableLiveData<String> feedback;


    // Database Reference
    private DatabaseReference databaseReference;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();



    // Constructor
    public CreateAccountViewModel() {
        title = new MutableLiveData<>("Create Account");
        emailHint = new MutableLiveData<>("Email");
        passHint = new MutableLiveData<>("Password");
        feedback = new MutableLiveData<>();


        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("accounts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // Required by interface - accounts are loaded elsewhere
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot,
                                       @Nullable String previousChildName) {
                // Required by interface - leave empty
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Leave Empty?
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot,
                                     @Nullable String previousChildName) {
                // Leave Empty?
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Leave Empty?
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

    public void updateFeedbackAccountCreation(boolean success) {
        if (success) {
            feedback.setValue("Account Successfully Created!");
        } else {
            feedback.setValue("Error in Account Creation!");
        }
    }

    // Method to create a new account and store it in the accountTable
    public boolean createNewAccount(String email, String password, Boolean isStaff) {

        try {
            // Check if the email is valid
            // First Check: check that none of the inputs are null
            if ((email == null) || (password == null) || (isStaff == null)) {
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

            // Should create AccountModel instance
            AccountModel.getInstance(email, password.hashCode(), isStaff);
            PairRegistry.register(
                    email,
                    new Pair<>(email, password.hashCode(), isStaff));

            // Add to Database
            databaseReference.child("accounts").child(
                    String.format("email%d", Pair.getNum())
            ).setValue(String.format("%s,%d,%b", email, password.hashCode(), isStaff));

            // Add a map to firestore
            Map<String, Object> user = new HashMap<>();
            user.put("uid", email);
            user.put("isStaff", isStaff);
            user.put("password", password.hashCode());
            db.collection("users").document(email).set(user);


            // Set Feedback Message
            updateFeedbackAccountCreation(true);
            return true;

        } catch (Exception e) {
            updateFeedbackAccountCreation(false);
        }
        return false;
    }
}

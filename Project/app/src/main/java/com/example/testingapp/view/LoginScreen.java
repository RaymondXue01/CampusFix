package com.example.testingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModelProvider;

import com.example.testingapp.R;
import com.example.testingapp.databinding.ActivityLoginScreenBinding;
import com.example.testingapp.viewmodel.LoginViewModel;


public class LoginScreen extends ThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up Data Binding
        ActivityLoginScreenBinding binding = ActivityLoginScreenBinding.inflate(
                getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Create ViewModel
        LoginViewModel viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Bind ViewModel
        binding.setVariable(BR.viewModel, viewModel);
        binding.setLifecycleOwner(this);

        // Find Login button by ID
        Button login = findViewById(R.id.login_button);

        // Find all interactable fields
        EditText emailField = findViewById(R.id.email_input);
        EditText passField = findViewById(R.id.password_input);

        // Set onClick Listener for button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text that was input into each field
                String emailInput = emailField.getText().toString();
                String password = passField.getText().toString();

                // Call attemptLogin method
                if (viewModel.attemptLogin(emailInput, password)) {
                    Intent intent = new Intent(LoginScreen.this, IssueFeed.class);
                    startActivity(intent);
                }
            }
        });

        // Find Create Account button by ID
        Button createAccountButton = findViewById(R.id.create_account_button);

        // Set onClick Listener for button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent to move to account creation screen
                Intent intent = new Intent(LoginScreen.this, CreateAccountScreen.class);
                startActivity(intent);
            }
        });
    }
}
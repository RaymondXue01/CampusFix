package com.example.testingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.testingapp.BR;
import com.example.testingapp.R;
import com.example.testingapp.databinding.ActivityCreateAccountScreenBinding;
import com.example.testingapp.viewmodel.CreateAccountViewModel;

public class CreateAccountScreen extends ThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up Data Binding
        ActivityCreateAccountScreenBinding binding = ActivityCreateAccountScreenBinding.inflate(
                getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Create ViewModel
        CreateAccountViewModel viewModel = new ViewModelProvider(
                this).get(CreateAccountViewModel.class);

        // Bind ViewModel
        binding.setVariable(BR.viewModel, viewModel);
        binding.setLifecycleOwner(this);

        // Find all interactable fields
        EditText emailField = findViewById(R.id.email_input);
        EditText passField = findViewById(R.id.password_input);
        CheckBox staff = findViewById(R.id.staff_checkbox);
        Button registerButton = findViewById(R.id.create_account_button);

        // Create an onClick listener for the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text that was input into each field
                String emailInput = emailField.getText().toString();
                String password = passField.getText().toString();

                // Get the current state of the checkbox
                Boolean isStaff = staff.isChecked();

                // Call createAccount method
                if (viewModel.createNewAccount(emailInput, password, isStaff)) {
                    // Call the issue feed screen and pass the value of "isStaff"
                    Intent intent = new Intent(CreateAccountScreen.this, IssueFeed.class);
                    intent.putExtra("isStaff", isStaff);
                    startActivity(intent);
                }
            }
        });
    }
}
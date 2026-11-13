package com.example.testingapp.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.viewmodel.IssueCreationViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class IssueCreation extends ThemeActivity {
    private EditText createTitle;
    private EditText createLocation;
    private EditText createDescription;
    private AutoCompleteTextView categoryDropdown;
    private AutoCompleteTextView priorityDropdown;
    private boolean isInputsValid = false;
    // Refactored to avoid declaring a String literal 5 times
    private static final String REQUIRED_FIELD = "This field is required";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IssueCreationViewModel viewModel =
                new ViewModelProvider(this).get(IssueCreationViewModel.class);
        String apiKey = getString(R.string.openai_api_key);
        viewModel.initConfig(apiKey);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_issue_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get isStaff
        Boolean isStaff = AccountModel.getInstance().getIsStaff();

        // If user is staff, then staff menu item will appear
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem staffMenuItem = menu.findItem(R.id.staff);
        staffMenuItem.setVisible(isStaff);

        bottomNavigationView.setSelectedItemId(R.id.create);

        //Observer creation
        viewModel.getIssueCreated().observe(this, issueCreated -> {
            if (issueCreated != null && issueCreated) {
                Toast.makeText(IssueCreation.this, "Form submitted!", Toast.LENGTH_LONG).show();
                // clears inputs
                clearInputs();
                viewModel.resetIssueCreatedStatus();
            } else if (issueCreated != null) {
                Toast.makeText(
                        IssueCreation.this,
                        "Form failed to submit!",
                        Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getModError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(IssueCreation.this, errorMsg, Toast.LENGTH_LONG).show();
                createDescription.setError(errorMsg);
            }
        });

        //should probably make this a method
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.create) {
                return true;
            } else if (id == R.id.profile) {
                Intent intent = new Intent(IssueCreation.this, ProfileScreen.class);
                startActivity(intent);
            } else if (id == R.id.feed) {
                Intent intent = new Intent(IssueCreation.this, IssueFeed.class);
                startActivity(intent);
            } else if (id == R.id.analytics) {
                Intent intent = new Intent(IssueCreation.this, AnalyticsScreen.class);
                startActivity(intent);
            } else if (id == R.id.staff) {
                Intent intent = new Intent(IssueCreation.this, StaffDashboard.class);
                startActivity(intent);
            } else {
                return false;
            }

            return true;
        });

        // Code for dropdown menus ----------------------------------------------------
        String[] categoryArr = getResources().getStringArray(R.array.category_level);
        String[] priorityArr = getResources().getStringArray(R.array.priority_level);

        categoryDropdown = findViewById(R.id.create_issue_category_dropdown);
        priorityDropdown = findViewById(R.id.create_issue_priority_dropdown);

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_item, categoryArr);
        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_item, priorityArr);

        categoryDropdown.setAdapter(categoryAdapter);
        priorityDropdown.setAdapter(priorityAdapter);

        categoryDropdown.setOnClickListener(v -> categoryDropdown.showDropDown());
        categoryDropdown.setKeyListener(null);
        priorityDropdown.setOnClickListener(v -> priorityDropdown.showDropDown());
        priorityDropdown.setKeyListener(null);

        // Code to check if each input is valid (not empty)
        // Refactored to move variable to be local to this method only
        Button buttonCreate;
        buttonCreate = findViewById(R.id.create_issue_button);

        createTitle = findViewById(R.id.create_issue_title);
        createLocation = findViewById(R.id.create_issue_location);
        createDescription = findViewById(R.id.create_issue_description);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInputsValid = validateInputs();

                if (isInputsValid) {
                    String title = createTitle.getText().toString();
                    String category = categoryDropdown.getText().toString();
                    String priority = priorityDropdown.getText().toString();
                    String location = createLocation.getText().toString();
                    String description = createDescription.getText().toString();
                    String uid = AccountModel.getInstance().getEmail1();
                    viewModel.createIssue(
                            title,
                            category,
                            priority,
                            location,
                            description,
                            uid);
                }
            }
        });
    }

    private boolean validateInputs() {
        if (createTitle.length() == 0) {
            createTitle.setError(REQUIRED_FIELD);
            return false;
        }
        if (createLocation.length() == 0) {
            createLocation.setError(REQUIRED_FIELD);
            return false;
        }
        if (createDescription.length() == 0) {
            createDescription.setError(REQUIRED_FIELD);
            return false;
        }
        if (categoryDropdown.getText().toString().isEmpty()) {
            categoryDropdown.setError(REQUIRED_FIELD);
            return false;
        }
        if (priorityDropdown.getText().toString().isEmpty()) {
            priorityDropdown.setError(REQUIRED_FIELD);
            return false;
        }
        return true;
    }
    private void clearInputs() {
        createTitle.setText("");
        createLocation.setText("");
        createDescription.setText("");

        categoryDropdown.setText("", false);
        categoryDropdown.setError(null);
        priorityDropdown.setText("");
        priorityDropdown.setError(null);

        createTitle.clearFocus();
    }
}
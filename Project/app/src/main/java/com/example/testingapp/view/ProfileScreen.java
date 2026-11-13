package com.example.testingapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.testingapp.R;
import com.example.testingapp.databinding.ActivityProfileScreenBinding;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.viewmodel.ProfileViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileScreen extends ThemeActivity {
    private AutoCompleteTextView themeDropdown;
    private static final String THEME = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up Data Binding
        ActivityProfileScreenBinding binding = ActivityProfileScreenBinding.inflate(
                getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Handle window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get isStaff from intent
        Boolean isStaff = AccountModel.getInstance().getIsStaff();

        // Create ViewModel
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setViewModel(profileViewModel);
        binding.setLifecycleOwner(this);

        // Set up bottom navigation
        setupBottomNavigation(isStaff);

        // Dropdown
        String[] themeArr = getResources().getStringArray(R.array.theme);
        themeDropdown = findViewById(R.id.theme_dropdown);
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item, themeArr);
        themeDropdown.setAdapter(themeAdapter);

        String email = AccountModel.getInstance().getEmail1();
        String prefName;
        if (email == null) {
            prefName = "default";
        } else {
            prefName = "settings_" + email;
        }

        SharedPreferences prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        int savedTheme = prefs.getInt(THEME, 0);
        if (savedTheme >= 0 && savedTheme < themeArr.length) {
            themeDropdown.setText(themeArr[savedTheme], false);
        }

        themeDropdown.setOnClickListener(v -> themeDropdown.showDropDown());
        themeDropdown.setKeyListener(null);

        // set up save theme button
        Button saveThemeBtn = findViewById(R.id.save_theme_button);
        saveThemeBtn.setOnClickListener(v ->  {
            String selectedTheme = themeDropdown.getText().toString();

            int themeValue = 0;
            if (selectedTheme.equals(themeArr[1])) {
                themeValue = 1;
            } else if (selectedTheme.equals(themeArr[2])) {
                themeValue = 2;
            } else if (selectedTheme.equals(themeArr[3])) {
                themeValue = 3;
            }

            String email1 = AccountModel.getInstance().getEmail1();
            if (email1 == null) {
                email1 = "default";
            }
            
            SharedPreferences userPrefs = getSharedPreferences("settings_" + email1, MODE_PRIVATE);
            int currTheme = userPrefs.getInt(THEME, 0);
            if (currTheme != themeValue) {
                userPrefs.edit().putInt(THEME, themeValue).apply();
                // Restart so all screens update
                recreate();
            }
        });


        // Set up logout button
        Button logout = findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileViewModel.logout();
                Intent intent = new Intent(ProfileScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Sets up the bottom navigation bar with click listeners for all screens.
     * Staff menu item is only visible to users with staff privileges.
     * @param isStaff Boolean indicating if current user is staff
     */
    private void setupBottomNavigation(Boolean isStaff) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();

        // Show/hide staff menu item based on role
        MenuItem staffMenuItem = menu.findItem(R.id.staff);
        if (staffMenuItem != null) {
            staffMenuItem.setVisible(isStaff);
        }

        // Set selected item to Profile
        bottomNavigationView.setSelectedItemId(R.id.profile);

        // Handle navigation clicks (simplified like StaffDashboard)
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Issue Feed
            if (id == R.id.feed) {
                Intent intent = new Intent(ProfileScreen.this, IssueFeed.class);
                startActivity(intent);
                finish();
                return true;
            }

            // Create Issue
            if (id == R.id.create) {
                Intent intent = new Intent(ProfileScreen.this, IssueCreation.class);
                startActivity(intent);
                finish();
                return true;
            }

            // Analytics
            if (id == R.id.analytics) {
                Intent intent = new Intent(ProfileScreen.this, AnalyticsScreen.class);
                startActivity(intent);
                finish();
                return true;
            }

            // Profile (current screen)
            if (id == R.id.profile) {
                return true;
            }

            // Staff Dashboard
            if (id == R.id.staff && isStaff) {
                Intent intent = new Intent(ProfileScreen.this, StaffDashboard.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }
}
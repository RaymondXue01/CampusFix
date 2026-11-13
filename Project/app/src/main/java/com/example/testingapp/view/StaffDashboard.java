package com.example.testingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.viewmodel.StaffDashboardViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StaffDashboard extends ThemeActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_dashboard);
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

        // Set selected to match the current page
        bottomNavigationView.setSelectedItemId(R.id.staff);

        // Link to other activities
        //should probably make this a method
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.create) {
                Intent intent = new Intent(StaffDashboard.this, IssueCreation.class);
                startActivity(intent);
            } else if (id == R.id.profile) {
                Intent intent = new Intent(StaffDashboard.this, ProfileScreen.class);
                startActivity(intent);
            } else if (id == R.id.feed) {
                Intent intent = new Intent(StaffDashboard.this, IssueFeed.class);
                startActivity(intent);
            } else if (id == R.id.analytics) {
                Intent intent = new Intent(StaffDashboard.this, AnalyticsScreen.class);
                startActivity(intent);
            } else {
                return false;
            }
            return true;
        });

        // Populate recyclerView
        RecyclerView recyclerView = findViewById(R.id.issue_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StaffDashboardAdapter adapter;
        adapter = new StaffDashboardAdapter();
        recyclerView.setAdapter(adapter);

        // Get the ViewModel
        StaffDashboardViewModel viewModel;
        viewModel = new ViewModelProvider(this).get(StaffDashboardViewModel.class);

        // Get the issues and update the adapter on data changes
        viewModel.getIssues().observe(this, issues -> adapter.setIssues(issues));

    }
}
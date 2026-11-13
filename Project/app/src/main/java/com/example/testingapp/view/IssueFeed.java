package com.example.testingapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.model.Issue;
import com.example.testingapp.viewmodel.IssueFeedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class IssueFeed extends ThemeActivity {

    private IssueFeedViewModel viewModel;
    private Button filterAll;
    private Button filterOpen;
    private Button filterCategory;
    private Spinner categorySpinner;
    private Button sortRecent;
    private Button sortPriority;
    private Button sortUpdated;
    private Button sortUpvotes;
    private IssueFeedAdapter adapter;

    private Button filterWatched;
    private String currentUserEmail = "";


    private boolean isCategoryFilterActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_issue_feed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user role (staff or student) from AccountModel singleton
        Boolean isStaff = AccountModel.getInstance().getIsStaff();

        // Set up RecyclerView with expandable adapter (tap to expand/collapse issue details)
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new IssueFeedAdapter();
        currentUserEmail = AccountModel.getInstance().getEmail1();
        adapter.setCurrentUserEmail(currentUserEmail);
        recyclerView.setAdapter(adapter);

        // Initialize ViewModel to handle issue data and filtering logic
        viewModel = new ViewModelProvider(this).get(IssueFeedViewModel.class);

        // Observe filtered issues from ViewModel and update adapter when data changes
        viewModel.getFilteredIssues().observe(this, issues -> adapter.submitList(issues));

        // Set up filter buttons (All Issues, Open Issues, Category)
        setupFilters();

        // Set up sort buttons (Most Recent, Highest Priority, Recently Updated)
        setupSortButtons();

        // Set up bottom navigation
        setupBottomNavigation(isStaff);
    }

    /**
     * Sets up the filter buttons and their click listeners.
     * Handles highlighting active filters and showing/hiding category spinner.
     */

    private void setupFilters() {
        filterAll = findViewById(R.id.filter_all);
        filterOpen = findViewById(R.id.filter_open);
        filterCategory = findViewById(R.id.filter_category);
        filterWatched = findViewById(R.id.filter_watched);
        categorySpinner = findViewById(R.id.category_spinner);

        // Set initial button colors
        updateFilterButtons("all");

        // All Issues button - shows all issues regardless of status
        filterAll.setOnClickListener(v -> {
            viewModel.setStatusFilter("all");
            viewModel.setCategoryFilter("all");
            updateFilterButtons("all");
            categorySpinner.setVisibility(View.GONE);
        });

        // Open Issues button - hides the closed and resolved issues
        filterOpen.setOnClickListener(v -> {
            viewModel.setStatusFilter("open");
            viewModel.setCategoryFilter("all");
            updateFilterButtons("open");
            categorySpinner.setVisibility(View.GONE);
        });

        // Category button
        filterCategory.setOnClickListener(v -> {
            if (categorySpinner.getVisibility() == View.VISIBLE) {
                // Closing dropdown - turn off teal
                categorySpinner.setVisibility(View.GONE);
                isCategoryFilterActive = false;
                updateFilterButtons("none"); // Reset to default
            } else {
                // Opening dropdown - turn on teal
                loadCategories();
                categorySpinner.setVisibility(View.VISIBLE);
                isCategoryFilterActive = true;
                updateFilterButtons("category");
            }
        });
        filterWatched.setOnClickListener(v -> {
            boolean nowActive = !viewModel.isWatchedFilterActive();
            viewModel.setWatchedFilter(nowActive, currentUserEmail);
            updateFilterButtons(nowActive ? "watched" : "none");
            if (nowActive) {
                categorySpinner.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Sets up the sort buttons and their click listeners.
     */
    private void setupSortButtons() {
        sortRecent = findViewById(R.id.sort_recent);
        sortPriority = findViewById(R.id.sort_priority);
        sortUpdated = findViewById(R.id.sort_updated);
        sortUpvotes = findViewById(R.id.sort_upvotes);

        // Observe current sort type to update button colors
        viewModel.getCurrentSortType().observe(this, sortType -> updateSortButtons(sortType));

        sortRecent.setOnClickListener(v -> {
            viewModel.setSortType("recent");
            Toast.makeText(this, "Sorting by Most Recent", Toast.LENGTH_SHORT).show();
        });

        sortPriority.setOnClickListener(v -> {
            viewModel.setSortType("priority");
            Toast.makeText(this, "Sorting by Highest Priority", Toast.LENGTH_SHORT).show();
        });

        sortUpdated.setOnClickListener(v -> {
            viewModel.setSortType("updated");
            Toast.makeText(this, "Sorting by Recently Updated", Toast.LENGTH_SHORT).show();
        });
        sortUpvotes.setOnClickListener(v -> {
            viewModel.setSortType("upvotes");
            Toast.makeText(this, "Sorting by Most Upvoted", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Updates sort button colors based on which sort is currently active.
     * @param active The active sort ("recent", "priority", or "updated")
     */
    private void updateSortButtons(String active) {
        // Reset all to default
        sortRecent.setBackgroundColor(getColor(R.color.design_default_color_primary));
        sortPriority.setBackgroundColor(getColor(R.color.design_default_color_primary));
        sortUpdated.setBackgroundColor(getColor(R.color.design_default_color_primary));
        sortUpvotes.setBackgroundColor(getColor(R.color.design_default_color_primary));


        // Highlight active button
        if ("recent".equals(active)) {
            sortRecent.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("priority".equals(active)) {
            sortPriority.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("updated".equals(active)) {
            sortUpdated.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("upvotes".equals(active)) {  // ADD THIS
            sortUpvotes.setBackgroundColor(getColor(R.color.teal_200));
        }
    }

    /**
     * Loads unique categories from ViewModel into the spinner dropdown.
     * Called when user clicks the Category button.
     */
    private void loadCategories() {
        List<String> categories = viewModel.getCategories();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if ("All Categories".equals(selected)) {
                    viewModel.setCategoryFilter("all");
                } else {
                    viewModel.setCategoryFilter(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                throw new UnsupportedOperationException();
            }
        });
    }

    /**
     * Updates filter button colors based on which filter is currently active.
     * Active buttons highlight in teal for better user feedback.
     * @param active The active filter ("all", "open", "category", or "none")
     */
    private void updateFilterButtons(String active) {
        filterAll.setBackgroundColor(getColor(R.color.design_default_color_primary));
        filterOpen.setBackgroundColor(getColor(R.color.design_default_color_primary));
        filterCategory.setBackgroundColor(getColor(R.color.design_default_color_primary));
        filterWatched.setBackgroundColor(getColor(R.color.design_default_color_primary));

        // Highlight active button
        if ("all".equals(active)) {
            filterAll.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("open".equals(active)) {
            filterOpen.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("category".equals(active)) {
            filterCategory.setBackgroundColor(getColor(R.color.teal_200));
        } else if ("watched".equals(active)) {
            filterWatched.setBackgroundColor(getColor(R.color.teal_200));
        }
    }

    /**
     * Sets up the bottom navigation bar with click listeners for all screens.
     * Staff menu item is only visible to users with staff privileges.
     * @param isStaff Boolean indicating if current user is staff
     */
    private void setupBottomNavigation(Boolean isStaff) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem staffMenuItem = menu.findItem(R.id.staff);
        staffMenuItem.setVisible(isStaff);

        bottomNavigationView.setSelectedItemId(R.id.feed);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.create) {
                Intent intent = new Intent(IssueFeed.this, IssueCreation.class);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.profile) {
                Intent intent = new Intent(IssueFeed.this, ProfileScreen.class);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.feed) {
                return true;
            } else if (id == R.id.analytics) {
                Intent intent = new Intent(IssueFeed.this, AnalyticsScreen.class);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.staff) {
                Intent intent = new Intent(IssueFeed.this, StaffDashboard.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null && adapter != null) {
            // Force adapter to clear and reload
            adapter.submitList(null);
            viewModel.refreshIssues();

            // Get fresh data and submit
            List<Issue> currentIssues = viewModel.getFilteredIssues().getValue();
            if (currentIssues != null) {
                adapter.submitList(new ArrayList<>(currentIssues));
            }
        }
    }
}
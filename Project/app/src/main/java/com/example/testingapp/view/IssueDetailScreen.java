package com.example.testingapp.view;
import android.content.res.ColorStateList;
import android.widget.Button;
import android.widget.EditText;
import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.testingapp.model.Issue;
import com.example.testingapp.viewmodel.IssueDetailViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import java.util.ArrayList;

public class IssueDetailScreen extends ThemeActivity {
    public static final String EXTRA_ISSUE = "extra_issue";
    private IssueDetailViewModel vm;
    private IssueUpdateAdapter ua;
    private EditText updateInput;

    @Override
    protected void onCreate(Bundle savedInstState) {
        super.onCreate(savedInstState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_issue_detail);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Issue issue = (Issue) getIntent().getSerializableExtra(EXTRA_ISSUE);
        if (issue == null) {
            Toast.makeText(this, "Error Loading the issue.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AccountModel account = AccountModel.getInstance();
        String currUserEmail = account.getEmail1();

        bindIssueSummary(issue);

        RecyclerView recyclerView = findViewById(R.id.updatesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ua = new IssueUpdateAdapter();
        recyclerView.setAdapter(ua);

        vm = new ViewModelProvider(this).get(IssueDetailViewModel.class);
        observeUpdates(issue.getId());
        setupInput(issue.getId(), currUserEmail);

        // ========== UP VOTE BUTTON ==========
        Button btnUpvote = findViewById(R.id.btnUpvote);
        TextView tvUpvoteCount = findViewById(R.id.tvUpvoteCount);

        // Set initial upvote count
        tvUpvoteCount.setText(String.valueOf(issue.getUpvotes()));

        // Initialize upvote state (like watch button does)
        boolean alreadyUpvoted = issue.getUpvotedBy()
                != null && issue.getUpvotedBy().contains(currUserEmail);
        vm.initUpvoteState(alreadyUpvoted);

        observeUpvote(btnUpvote, currUserEmail, issue);

        // Check if user already upvoted this issue
        // Check if user already upvoted this issue
        // Check if user already upvoted this issue - use array for mutable reference
        boolean[] hasUpvoted = {issue.getUpvotedBy() != null
                && issue.getUpvotedBy().contains(currUserEmail)};

        btnUpvote.setOnClickListener(v -> {
            // Call ViewModel to toggle upvote (observer will handle text and color)
            vm.toggleUpvote(issue.getId(), currUserEmail);

            // Update UI optimistically (just the count and local state)
            if (hasUpvoted[0]) {
                // Currently upvoted, so removing
                if (issue.getUpvotedBy() != null) {
                    issue.getUpvotedBy().remove(currUserEmail);
                }
                issue.setUpvotes(Math.max(0, issue.getUpvotes() - 1));
                hasUpvoted[0] = false;
            } else {
                // Not upvoted, so adding
                if (issue.getUpvotedBy() == null) {
                    issue.setUpvotedBy(new ArrayList<>());
                }
                issue.getUpvotedBy().add(currUserEmail);
                issue.setUpvotes(issue.getUpvotes() + 1);
                hasUpvoted[0] = true;
            }
            tvUpvoteCount.setText(String.valueOf(issue.getUpvotes()));
        });
        // Watch Button //
        Button btnWatch = findViewById(R.id.btnWatch);
        TextView watcherCount = findViewById(R.id.tvWatcherCount);
        watcherCount.setText(String.valueOf(issue.getWatchers()));

        if (currUserEmail != null && currUserEmail.equals(issue.getUid())) {
            btnWatch.setEnabled(false);
            btnWatch.setText("Your Issues");
        } else {
            boolean alreadyWatching = issue.getWatchedBy() != null
                    && issue.getWatchedBy().contains(currUserEmail);
            vm.initWatchState(alreadyWatching);

            observeWatch(btnWatch, btnUpvote, watcherCount, issue, currUserEmail);
        }
    }

    private void observeUpdates(String issueId) {
        vm.getUpdates(issueId).observe(this, updates -> ua.setIssueUpdate(updates));
        vm.getUpdateAdded().observe(this, success -> {
            if (success == null) {
                return;
            }
            if (success) {
                Toast.makeText(this, "Update added!", Toast.LENGTH_SHORT).show();
                updateInput.setText("");
            } else {
                Toast.makeText(this, "Failed to add update.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void observeWatch(Button btnWatch,
                              Button btnUpvote,
                              TextView tvWatcherCount,
                              Issue issue,
                              String currUserEmail) {
        android.content.res.ColorStateList defaultTint = btnUpvote.getBackgroundTintList();
        vm.getIsWatching().observe(this, watching -> {
            if (watching == null) {
                return;
            }
            if (watching) {
                btnWatch.setText("Watching");
                btnWatch.setBackgroundTintList(
                        getColorStateList(R.color.design_default_color_primary));
            } else {
                btnWatch.setText("Watch");
                btnWatch.setBackgroundTintList(defaultTint);
            }
            btnWatch.setEnabled(true);
        });
        vm.getWatchError().observe(this, error -> {
            if (error == null) {
                return;
            }
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            btnWatch.setEnabled(true);
        });

        btnWatch.setOnClickListener(v -> {
            btnWatch.setEnabled(false);
            vm.toggleWatch(issue.getId(), currUserEmail);
            Boolean currentlyWatching = vm.getIsWatching().getValue();
            if (Boolean.TRUE.equals(currentlyWatching)) {
                tvWatcherCount.setText(String.valueOf(
                        Math.max(0, issue.getWatchers() - 1)));
                issue.setWatchers(Math.max(0, issue.getWatchers() - 1));
            } else {
                tvWatcherCount.setText(String.valueOf(issue.getWatchers() + 1));
                issue.setWatchers(issue.getWatchers() + 1);
            }
        });
    }
    private void observeUpvote(Button btnUpvote, String currUserEmail, Issue issue) {
        // Get default tint (same as watch button does)
        ColorStateList defaultTint = btnUpvote.getBackgroundTintList();

        vm.getIsUpvoted().observe(this, isUpvoted -> {
            if (isUpvoted == null) {
                return;
            }

            if (isUpvoted) {
                btnUpvote.setText("Upvoted");
                btnUpvote.setBackgroundTintList(
                        getColorStateList(R.color.design_default_color_primary));
            } else {
                btnUpvote.setText("Upvote");
                btnUpvote.setBackgroundTintList(defaultTint);
            }
            btnUpvote.setEnabled(true);
        });
    }
    private void bindIssueSummary(Issue issue) {
        TextView titleView = findViewById(R.id.issueTitleDetail);
        TextView statusView = findViewById(R.id.issueStatusDetail);
        TextView categoryView = findViewById(R.id.issueCategoryDetail);
        TextView priorityView = findViewById(R.id.issuePriorityDetail);
        TextView locationView = findViewById(R.id.issueLocationDetail);
        TextView descriptionView = findViewById(R.id.issueDescriptionDetail);

        if (issue.getTitle() != null) {
            titleView.setText(issue.getTitle());
        } else {
            titleView.setText("");
        }

        if (issue.getStatus() != null) {
            statusView.setText("Status: " + issue.getStatus());
        } else {
            statusView.setText("Status: N/A");
        }

        if (issue.getCategory() != null) {
            categoryView.setText("Category: " + issue.getCategory());
        } else {
            categoryView.setText("Category: N/A");
        }

        if (issue.getPriority() != null) {
            priorityView.setText("Priority: " + issue.getPriority());
        } else {
            priorityView.setText("Priority: N/A");
        }

        if (issue.getLocation() != null) {
            locationView.setText("Location: " + issue.getLocation());
        } else {
            locationView.setText("Location: N/A");
        }

        if (issue.getDescription() != null) {
            descriptionView.setText("Description: " + issue.getDescription());
        } else {
            descriptionView.setText("Description: N/A");
        }
    }

    private void setupInput(String issueId, String currentUserEmail) {
        Button addComment;

        updateInput = findViewById(R.id.updateInput);
        addComment = findViewById(R.id.btnAddComment);


        addComment.setOnClickListener(v -> {
            String text = updateInput.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter a comment.", Toast.LENGTH_SHORT).show();
                return;
            }
            vm.addComment(issueId, currentUserEmail, text);
        });
    }

}

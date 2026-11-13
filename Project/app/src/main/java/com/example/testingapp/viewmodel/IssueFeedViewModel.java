package com.example.testingapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

public class IssueFeedViewModel extends ViewModel {
    private static final String RECENT = "recent";
    private final IssueRepository repository = IssueRepository.getInstance();
    private MutableLiveData<List<Issue>> filteredIssues = new MutableLiveData<>();
    private MutableLiveData<String> currentStatusFilter = new MutableLiveData<>("all");
    private MutableLiveData<String> currentCategoryFilter = new MutableLiveData<>("all");
    private MutableLiveData<String> currentSortType = new MutableLiveData<>(RECENT);
    private List<Issue> allIssues = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private String currentUserEmail = "";
    private boolean watchedFilterActive = false;
    public IssueFeedViewModel() {
        filteredIssues.setValue(new ArrayList<>());

        repository.getTheIssues().observeForever(issues -> {
            if (issues != null) {
                allIssues = new ArrayList<>(issues);
                updateCategories();
                applyFiltersAndSort();
            }
        });
    }

    private void updateCategories() {
        categories.clear();
        categories.add("All Categories");
        for (Issue issue : allIssues) {
            String cat = issue.getCategory();
            if (cat != null && !cat.isEmpty() && !categories.contains(cat)) {
                categories.add(cat);
            }
        }
        if (categories.size() > 1) {
            Collections.sort(categories.subList(1, categories.size()));
        }
    }

    public void applyFiltersAndSort() {
        List<Issue> filtered = new ArrayList<>(allIssues);

        // Step 1: Apply filters
        String statusFilter = currentStatusFilter.getValue();
        if ("open".equals(statusFilter)) {
            filtered.removeIf(issue ->
                    "Closed".equals(issue.getStatus()));
        }

        String categoryFilter = currentCategoryFilter.getValue();
        if (categoryFilter != null && !"all".equals(categoryFilter)
                && !"All Categories".equals(categoryFilter)) {
            filtered.removeIf(issue -> !categoryFilter.equals(issue.getCategory()));
        }
        // Step 2: Watched Filter: only shows current issues the user is watching
        if (watchedFilterActive && !currentUserEmail.isEmpty()) {
            filtered.removeIf(issue -> {
                List<String> watchers = issue.getWatchedBy();
                return watchers == null || !watchers.contains(currentUserEmail);
            });
        }

        // Step 3: Apply sorting (filters + sort work together)
        String sortType = currentSortType.getValue();
        applySorting(filtered, sortType);

        filteredIssues.setValue(filtered);
    }

    private void applySorting(List<Issue> issues, String sortType) {
        if (sortType == null) {
            sortType = RECENT;
        }

        switch (sortType) {
        case "priority":
            // Highest priority first (1 = High, 4 = Low)
            issues.sort((i1, i2) -> {
                int p1 = getPriorityValue(i1.getPriority());
                int p2 = getPriorityValue(i2.getPriority());
                return Integer.compare(p1, p2);
            });
            break;

        case "updated":
            // Most recently updated first
            issues.sort((i1, i2) -> {
                Date d1 = i1.getLastUpdated() != null ? i1.getLastUpdated() : i1.getTimestamp();
                Date d2 = i2.getLastUpdated() != null ? i2.getLastUpdated() : i2.getTimestamp();
                if (d1 == null && d2 == null) {
                    return 0;
                }
                if (d1 == null) {
                    return -1;
                }
                if (d2 == null) {
                    return 1;
                }
                return d2.compareTo(d1);
            });
            break;

        case "upvotes":  // ADD THIS CASE
            // Most upvotes first (highest to lowest)
            issues.sort((i1, i2) -> Integer.compare(i2.getUpvotes(), i1.getUpvotes()));
            break;

        case RECENT:
        default:
            // Most recent first (default)
            issues.sort((i1, i2) -> {
                Date d1 = i1.getTimestamp();
                Date d2 = i2.getTimestamp();
                if (d1 == null && d2 == null) {
                    return 0;
                }
                if (d1 == null) {
                    return -1;
                }
                if (d2 == null) {
                    return 1;
                }
                return d2.compareTo(d1);
            });
            break;
        }
    }

    private int getPriorityValue(String priority) {
        if (priority == null) {
            return 4;  // null = lowest priority
        }
        if (priority.contains("1") || priority.equalsIgnoreCase("High")) {
            return 1;  // Highest
        }
        if (priority.contains("2") || priority.equalsIgnoreCase("Medium")) {
            return 2;
        }
        if (priority.contains("3") || priority.equalsIgnoreCase("Low")) {
            return 3;
        }
        if (priority.contains("4")) {
            return 4;  // Lowest
        }
        return 4;
    }
    public boolean isWatchedFilterActive() {
        return watchedFilterActive;
    }

    public void setStatusFilter(String filter) {
        currentStatusFilter.setValue(filter);
        applyFiltersAndSort();
    }

    public void setCategoryFilter(String category) {
        currentCategoryFilter.setValue(category);
        applyFiltersAndSort();
    }
    public void setWatchedFilter(boolean active, String userEmail) {
        this.watchedFilterActive = active;
        this.currentUserEmail = userEmail != null ? userEmail : "";
        applyFiltersAndSort();
    }

    public void setSortType(String sortType) {
        currentSortType.setValue(sortType);
        applyFiltersAndSort();
    }
    public void refreshIssues() {
        // Force a refresh by re-applying filters and sort
        applyFiltersAndSort();
    }

    public LiveData<List<Issue>> getFilteredIssues() {
        return filteredIssues;
    }

    public List<String> getCategories() {
        return categories;
    }

    public LiveData<String> getCurrentSortType() {
        return currentSortType;
    }
}
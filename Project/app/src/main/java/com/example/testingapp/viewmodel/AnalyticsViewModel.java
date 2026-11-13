package com.example.testingapp.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AnalyticsViewModel extends ViewModel {
    private final MutableLiveData<String> headerTitle;
    private final IssueRepository repository = IssueRepository.getInstance();

    private MutableLiveData<HashMap<String, Integer>> statusCounts =
            new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<HashMap<String, Integer>> categoryCounts =
            new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<HashMap<String, Integer>> timeCounts =
            new MutableLiveData<>(new HashMap<>());

    /**
     * Similar to constructor from IssueFeedViewModel.
     * Constructor for AnalyticsViewModel Class
     */
    public AnalyticsViewModel() {
        // Initialize MutableLiveData
        headerTitle = new MutableLiveData<>();
        // Set title to Analytics
        headerTitle.setValue("Analytics");

        // Observe all issues from repository
        repository.getTheIssues().observeForever(issues -> {
            if (issues != null) {
                calculateStatusCounts(issues);
                calculateCategoryCounts(issues);
                calculateTimeCounts(issues);
            }
        });
    }

    private void calculateStatusCounts(List<Issue> issues) {
        HashMap<String, Integer> counts = new HashMap<>();
        // loop through issues, grabs status & updates count
        for (Issue issue : issues) {
            String status = issue.getStatus();
            if (status == null || status.isEmpty()) {
                status = "No Status";
            }
            counts.put(status, counts.getOrDefault(status, 0) + 1);
        }
        statusCounts.setValue(counts);
    }

    private void calculateCategoryCounts(List<Issue> issues) {
        HashMap<String, Integer> counts = new HashMap<>();
        // loop through issues, grabs category & updates count
        for (Issue issue : issues) {
            String category = issue.getCategory();
            if (category == null || category.isEmpty()) {
                category = "No Category";
            }
            counts.put(category, counts.getOrDefault(category, 0) + 1);
        }
        categoryCounts.setValue(counts);
    }

    private void calculateTimeCounts(List<Issue> issues) {
        HashMap<String, Integer> counts = new HashMap<>();
        // loop through issues, grabs & formats the time then updates count
        for (Issue issue : issues) {
            Date time = issue.getTimestamp();
            String timestampFormat = "";
            if (time == null) {
                timestampFormat = "No Timestamp";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                timestampFormat = sdf.format(time);
            }
            counts.put(timestampFormat, counts.getOrDefault(timestampFormat, 0) + 1);
        }
        timeCounts.setValue(counts);
    }

    // Getter method for LiveData for status & it's count
    public LiveData<HashMap<String, Integer>> getStatusCounts() {
        return statusCounts;
    }

    // Getter method for LiveData for category & it's count
    public LiveData<HashMap<String, Integer>> getCategoryCounts() {
        return categoryCounts;
    }

    // Getter method for LiveData for time & it's count
    public LiveData<HashMap<String, Integer>> getTimeCounts() {
        return timeCounts;
    }

    // Getter method for LiveData
    public LiveData<String> getHeaderText() {
        return headerTitle;
    }
}

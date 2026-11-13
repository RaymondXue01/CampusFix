package com.example.testingapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueRepository;

import java.util.ArrayList;
import java.util.List;


public class StaffDashboardViewModel extends ViewModel {
    private final IssueRepository repository = IssueRepository.getInstance();
    private final MutableLiveData<List<Issue>> allIssues = new MutableLiveData<>();

    public LiveData<List<Issue>> getIssues() {
        return allIssues;
    }

    public StaffDashboardViewModel() {
        allIssues.setValue(new ArrayList<>());
        repository.getTheIssues().observeForever(issues -> {
            if (issues != null) {
                allIssues.setValue(issues);
            }
        });
    }
}

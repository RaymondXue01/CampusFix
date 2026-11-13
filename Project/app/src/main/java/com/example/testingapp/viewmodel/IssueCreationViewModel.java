package com.example.testingapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueRepository;
import com.example.testingapp.model.TextModRepository;

public class IssueCreationViewModel extends ViewModel {
    private MutableLiveData<Boolean> issueCreated = new MutableLiveData<>();
    private IssueRepository issueRepository = IssueRepository.getInstance();
    // text moderation
    private TextModRepository textModRepository;
    private MutableLiveData<String> textModError = new MutableLiveData<>();

    public LiveData<String> getModError() {
        return textModError;
    }

    // method to initialize repository with key
    public void initConfig(String apiKey) {
        if (textModRepository == null) {
            textModRepository = new TextModRepository(apiKey);
        }
    }

    public MutableLiveData<Boolean> getIssueCreated() {
        return issueCreated;
    }

    public void createIssue(String title,
                            String category,
                            String priority,
                            String location,
                            String description,
                            String uid) {
        // before creating issue, check moderation
        textModRepository.checkText(description, new TextModRepository.TextModCallback() {
            @Override
            public void onResult(boolean isFlagged) {
                if (isFlagged) {
                    textModError.setValue("Inappropriate content detected in description.");
                } else {
                    // safe to add creation logic
                    saveIssueToFirestore(title, category, priority, location, description, uid);
                }
            }
            @Override
            public void onError(String error) {
                if (error.contains("429")) {
                    textModError.setValue("Too many requests. Please try again later.");
                    return;
                }
                textModError.setValue("Could not verify content: " + error);
            }
        });
    }

    private void saveIssueToFirestore(String title,
                                      String category,
                                      String priority,
                                      String location,
                                      String description,
                                      String uid) {
        Issue issue = new Issue(title, category, priority, location, description, uid);

        issueRepository.addIssue(issue, task -> {
            if (task.isSuccessful()) {
                issueCreated.setValue(true);
            } else {
                issueCreated.setValue(false);
            }
        });
    }

    public void resetIssueCreatedStatus() {
        issueCreated.setValue(null);
    }
}

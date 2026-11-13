package com.example.testingapp.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.testingapp.model.IssueRepository;

import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueUpdateRepository;
import com.example.testingapp.model.IssueUpdate;
import java.util.List;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class IssueDetailViewModel extends ViewModel {
    private final IssueUpdateRepository repository = IssueUpdateRepository.getInstance();
    private LiveData<List<IssueUpdate>> updates;
    private final MutableLiveData<Boolean> updateAdded = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> upvoteError = new MutableLiveData<>();
    private final IssueRepository issueRepo = IssueRepository.getInstance();
    private final MutableLiveData<Boolean> isWatching = new MutableLiveData<>();
    private final MutableLiveData<String> watchError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpvoted = new MutableLiveData<>();


    private static final String UPVOTE = "Upvote";

    public LiveData<List<IssueUpdate>> getUpdates(String issueId) {
        if (updates == null) {
            updates = repository.getUpdates(issueId);
        }
        return updates;
    }
    public LiveData<Boolean> getUpdateAdded() {
        return updateAdded;
    }
    public LiveData<String> getUpvoteError() {
        return upvoteError;
    }
    public LiveData<Boolean> getIsUpvoted() {
        return isUpvoted;
    }

    public void addComment(String issueId, String author, String content) {
        IssueUpdate update = new IssueUpdate(author, IssueUpdate.TYPE_COMMENT, content);
        repository.addUpdate(issueId, update, task -> updateAdded.setValue(task.isSuccessful()));
    }

    public void addStaffNote(String issueId, String author, String content, boolean isStaff) {
        if (!isStaff) {
            updateAdded.setValue(false);
            return;
        }
        IssueUpdate update = new IssueUpdate(author, IssueUpdate.TYPE_STAFF_NOTE, content);
        repository.addUpdate(issueId, update, task -> updateAdded.setValue(task.isSuccessful()));
    }
    public LiveData<Boolean> getIsWatching() {
        return isWatching;
    }
    public LiveData<String> getWatchError() {
        return watchError;
    }
    public void recordingStatusChange(String issueId,
                                      String author,
                                      String newStatus,
                                      boolean isStaff) {
        if (!isStaff) {
            updateAdded.setValue(false);
            return;
        }
        String content = "Status changed to: " + newStatus;
        IssueUpdate update = new IssueUpdate(author, IssueUpdate.TYPE_STATUS_CHANGE, content);
        repository.addUpdate(issueId, update, task -> updateAdded.setValue(task.isSuccessful()));
    }
    public void initWatchState(boolean currentlyWatching) {
        isWatching.setValue(currentlyWatching);
    }

    public void initUpvoteState(boolean currentlyUpvoted) {
        isUpvoted.setValue(currentlyUpvoted);
    }
    public void toggleUpvote(String issueId, String userEmail) {
        DocumentReference issueRef = db.collection("issues").document(issueId);

        // Use transaction to prevent race conditions
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(issueRef);

            Issue issue = snapshot.toObject(Issue.class);

            if (issue == null) {
                throw new IllegalStateException("Issue not found");
            }

            // Check if issue is closed - prevent upvoting closed issues
            if (issue.isClosed()) {
                throw new IllegalStateException("Cannot upvote closed issues");

            }

            List<String> upvotedBy = issue.getUpvotedBy();
            if (upvotedBy == null) {
                upvotedBy = new ArrayList<>();
            }

            boolean wasAdded;
            if (upvotedBy.contains(userEmail)) {
                upvotedBy.remove(userEmail);
                wasAdded = false;
            } else {
                upvotedBy.add(userEmail);
                wasAdded = true;
            }


            transaction.update(issueRef, "upvotedBy", upvotedBy, "upvotes", upvotedBy.size());
            return wasAdded;
        }).addOnSuccessListener(wasAdded -> {
            // Update the LiveData to notify UI (true = upvoted, false = not upvoted)
            isUpvoted.setValue(wasAdded);
        }).addOnFailureListener(e -> {
            upvoteError.setValue("Upvote failed: " + e.getMessage());
        });
    }
    public void toggleWatch(String issueId, String userEmail) {
        issueRepo.toggleWatch(issueId, userEmail, task -> {
            if (task.isSuccessful()) {
                Boolean curr = isWatching.getValue();
                isWatching.setValue(curr == null || !curr); // Fixes unnecessary boolean literal
            } else {
                watchError.setValue("Failed to update watch status. Please try again.");
            }
        });
    }
}

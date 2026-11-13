package com.example.testingapp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentReference;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.ArrayList;

public class IssueRepository {
    private final FirebaseFirestore db;
    private static IssueRepository inst;

    private static final String ISSUES = "issues"; // Avoids define 3 separate literals

    private IssueRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static IssueRepository getInstance() {
        if (inst == null) {
            inst = new IssueRepository();
        }
        return inst;
    }

    public LiveData<List<Issue>> getTheIssues() {
        MutableLiveData<List<Issue>> liveData = new MutableLiveData<>();

        db.collection(ISSUES)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        liveData.setValue(new ArrayList<>());

                        return;
                    }
                    List<Issue> issues = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Issue issue = doc.toObject(Issue.class);
                        if (issue != null) {
                            issue.setId(doc.getId());
                            issues.add(issue);
                        }
                    }
                    liveData.setValue(issues);
                });
        return  liveData;
    }

    public void addIssue(Issue issue, OnCompleteListener<DocumentReference> listener) {
        db.collection(ISSUES)
                .add(issue)
                .addOnCompleteListener(listener);
    }
    public void updateField(String issueId,
                            UpdateType type,
                            String updateContent,
                            OnCompleteListener<Void> listener) {
        db.collection(ISSUES)
                .document(issueId)
                .update(type.getType(), updateContent)
                .addOnCompleteListener(listener);
    }

    public void toggleWatch(String issueId,
                            String userEmail,
                            OnCompleteListener<Void> listener) {
        DocumentReference issueRef = db.collection(ISSUES).document(issueId);
        issueRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                return;
            }
            Issue issue = documentSnapshot.toObject(Issue.class);
            if (issue == null) {
                return;
            }
            List<String> watchedBy = issue.getWatchedBy();
            if (watchedBy == null) {
                watchedBy = new ArrayList<>();
            }
            if (watchedBy.contains(userEmail)) {
                watchedBy.remove(userEmail);
            } else {
                watchedBy.add(userEmail);
            }

            issueRef.update("watchedBy", watchedBy, "watchers", watchedBy.size())
                    .addOnCompleteListener(listener);
        }).addOnFailureListener(e -> {

        });
    }
}

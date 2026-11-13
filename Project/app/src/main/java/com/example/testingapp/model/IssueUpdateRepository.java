package com.example.testingapp.model;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import java.util.List;
import java.util.ArrayList;
public class IssueUpdateRepository {
    private static IssueUpdateRepository inst;
    private  final FirebaseFirestore db;

    private IssueUpdateRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static IssueUpdateRepository getInstance() {
        if (inst == null) {
            inst = new IssueUpdateRepository();
        }
        return inst;
    }

    public LiveData<List<IssueUpdate>> getUpdates(String issueId) {
        MutableLiveData<List<IssueUpdate>> liveData = new MutableLiveData<>();

        db.collection("issues")
                .document(issueId)
                .collection("updates")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null || snapshot == null) {
                        liveData.setValue(new ArrayList<>());
                        return;
                    }

                    List<IssueUpdate> updates = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        IssueUpdate update = doc.toObject(IssueUpdate.class);
                        if (update != null) {
                            updates.add(update);
                        }
                    }
                    liveData.setValue(updates);
                });

        return liveData;
    }

    public void addUpdate(String issueId,
                          IssueUpdate update,
                          OnCompleteListener<DocumentReference> listener) {
        db.collection("issues")
                .document(issueId)
                .collection("updates")
                .add(update)
                .addOnCompleteListener(listener);
    }

}
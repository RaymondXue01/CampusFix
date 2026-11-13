package com.example.testingapp.view;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.example.testingapp.R;
import com.example.testingapp.model.IssueUpdate;
public class IssueUpdateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int STATUS_CHANGE = 0;
    private static final int STAFF_NOTE = 1;
    private static final int COMMENT = 2;
    private List<IssueUpdate> issueUpdate = new ArrayList<>();

    public void setIssueUpdate(List<IssueUpdate> newestUpdates) {
        if (newestUpdates != null) {
            this.issueUpdate = newestUpdates;
        } else {
            this.issueUpdate = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int index) {
        String type = issueUpdate.get(index).getType();
        if (IssueUpdate.TYPE_STATUS_CHANGE.equals(type)) {
            return STATUS_CHANGE;
        } else if (IssueUpdate.TYPE_STAFF_NOTE.equals(type)) {
            return STAFF_NOTE;
        } else {
            return COMMENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == STAFF_NOTE) {
            View view = inflater.inflate(R.layout.item_update_staff_note, parent, false);
            return new UpdateViewHolder(view);
        } else if (viewType == STATUS_CHANGE) {
            View view = inflater.inflate(R.layout.item_update_status_change, parent, false);
            return new UpdateViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_update_comment, parent, false);
            return new UpdateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        IssueUpdate newuUpdate = issueUpdate.get(pos);
        UpdateViewHolder viewHolder = (UpdateViewHolder) holder;
        if (newuUpdate.getContent() != null) {
            viewHolder.content.setText(newuUpdate.getContent());
        } else {
            viewHolder.content.setText("");
        }
        /// ////
        if (newuUpdate.getAuthor() != null) {
            viewHolder.author.setText("By: " + newuUpdate.getAuthor());
        } else {
            viewHolder.author.setText("By: Unknown");
        }
        /// ///
        if (newuUpdate.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            viewHolder.timestamp.setText(sdf.format(newuUpdate.getTimestamp()));
        } else {
            viewHolder.timestamp.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return issueUpdate.size();
    }

    static class UpdateViewHolder extends RecyclerView.ViewHolder {
        private TextView content;
        private TextView author;
        private TextView timestamp;
        UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.updateContent);
            author = itemView.findViewById(R.id.updateAuthor);
            timestamp = itemView.findViewById(R.id.updateTimestamp);
        }
    }

}
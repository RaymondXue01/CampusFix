package com.example.testingapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingapp.R;
import com.example.testingapp.model.AccountModel;
import com.example.testingapp.model.Issue;
import com.example.testingapp.model.IssueRepository;
import com.example.testingapp.model.IssueUpdate;
import com.example.testingapp.model.IssueUpdateRepository;
import com.example.testingapp.model.StaffAssignmentUpdate;
import com.example.testingapp.model.StaffNoteUpdate;
import com.example.testingapp.model.StatusUpdate;
import com.example.testingapp.model.UpdateType;

import java.util.ArrayList;
import java.util.List;

public class StaffDashboardAdapter
        extends RecyclerView.Adapter<StaffDashboardAdapter.IssueViewHolder> {
    // Attributes
    private List<Issue> issues = new ArrayList<>();
    private final String name;

    public StaffDashboardAdapter() {
        this.name = AccountModel.getInstance().getEmail1();
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_dashboard_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffDashboardAdapter.IssueViewHolder holder,
                                 int position) {
        holder.bind(issues.get(position), name);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    static class IssueViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final EditText etAssignedStaff;
        private final TextView tvIssueStatus;
        private final Spinner spinnerStatus;
        private final EditText etStaffNote;
        private final Button btnUpdateIssue;
        IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.staff_issue_title);
            etAssignedStaff = itemView.findViewById(R.id.staff_assigned_staff_input);
            tvIssueStatus = itemView.findViewById(R.id.staff_issue_status_static);
            spinnerStatus = itemView.findViewById(R.id.staff_issue_status);
            etStaffNote = itemView.findViewById(R.id.staff_note_field);
            btnUpdateIssue = itemView.findViewById(R.id.update_issue_individual);
        }

        void bind(Issue issue, String staffName) {
            tvTitle.setText(issue.getTitle());
            // Fill the staff field if there is already a staff member assigned to an issue
            if (issue.getAssignedStaff() != null && !issue.getAssignedStaff().isEmpty()) {
                etAssignedStaff.setText(issue.getAssignedStaff().trim());
            } else {
                etAssignedStaff.setText("");
            }
            tvIssueStatus.setText(issue.getStatus());
            // Fill the note field with the most recent staff note if it exits
            if (issue.getStaffNote() != null && !issue.getStaffNote().isEmpty()) {
                etStaffNote.setText(issue.getStaffNote());
            } else {
                etStaffNote.setText("");
            }
            // Disable editing if an issue is closed
            etAssignedStaff.setEnabled(!issue.isClosed());
            etStaffNote.setEnabled(!issue.isClosed());
            // If no available next status, hide the spinner
            if (issue.isClosed()) {
                spinnerStatus.setVisibility(View.GONE);
                btnUpdateIssue.setVisibility(View.GONE);
            } else {
                spinnerStatus.setVisibility(View.VISIBLE);
                Issue.Status currentStatus = Issue.Status.fromString((issue.getStatus()));
                if (currentStatus == null) {
                    spinnerStatus.setVisibility(View.GONE);
                } else {
                    String nextStatus = currentStatus.getNext().getCurrentStatus();
                    List<String> statusList = new ArrayList<>();
                    statusList.add(currentStatus.getCurrentStatus());
                    statusList.add(nextStatus);
                    // Create a new ArrayAdapter
                    ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                            itemView.getContext(), R.layout.staff_dashboard_spinner, statusList);
                    statusAdapter.setDropDownViewResource(R.layout.staff_dashboard_spinner);
                    spinnerStatus.setAdapter(statusAdapter);
                }
            }
            btnUpdateIssue.setOnClickListener(v -> {
                String noteText = etStaffNote.getText().toString().trim();
                // Only push a staff note if the field is not-empty
                if (!noteText.isEmpty()) {
                    IssueUpdate noteUpdate = new IssueUpdate(
                            staffName, IssueUpdate.TYPE_STAFF_NOTE, noteText);
                    IssueUpdateRepository.getInstance().addUpdate(
                            issue.getId(), noteUpdate, task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(itemView.getContext(),
                                            "Failed to save note",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    // Save latest note to the issue itself
                    UpdateType type = new StaffNoteUpdate();
                    IssueRepository.getInstance().updateField(
                            issue.getId(), type, noteText, task -> {
                                if (task.isSuccessful()) {
                                    issue.setStaffNote(noteText);
                                } else {
                                    Toast.makeText(itemView.getContext(),
                                            "Failed to reach database to update issue",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }
                // Only process a status change if the issue isn't closed
                if (!issue.isClosed()) {
                    String newStatus = spinnerStatus.getSelectedItem().toString();
                    if (!newStatus.equals(issue.getStatus())) {
                        UpdateType type = new StatusUpdate();
                        String content = String.format(
                                "Status changed from %s to %s", issue.getStatus(), newStatus);
                        IssueUpdate statusUpdate = new IssueUpdate(
                                staffName, IssueUpdate.TYPE_STATUS_CHANGE, content
                        );
                        IssueUpdateRepository.getInstance().addUpdate(
                                issue.getId(), statusUpdate, task -> {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(itemView.getContext(),
                                                "Failed to log status change",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        IssueRepository.getInstance().updateField(
                                issue.getId(), type, newStatus, task -> {
                                    if (task.isSuccessful()) {
                                        // Set new Status
                                        issue.setStatus(newStatus);
                                        tvIssueStatus.setText(newStatus);
                                        etAssignedStaff.setEnabled(!issue.isClosed());
                                        if (issue.isClosed()) {
                                            spinnerStatus.setVisibility(View.GONE);
                                        } else {
                                            Issue.Status updated = Issue.Status
                                                    .fromString((newStatus));
                                            String next = updated.getNext().getCurrentStatus();
                                            ((ArrayAdapter<String>) spinnerStatus.getAdapter()
                                            ).clear();
                                            ((ArrayAdapter<String>) spinnerStatus.getAdapter()
                                            ).add(updated.getCurrentStatus());
                                            ((ArrayAdapter<String>) spinnerStatus.getAdapter()
                                            ).add(next);
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(),
                                                "Failed to update status",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }
                // Update assigned staff if the field has changed
                String newStaffAssignment = etAssignedStaff.getText().toString().trim();
                if (!newStaffAssignment.equals(issue.getAssignedStaff())) {
                    UpdateType type = new StaffAssignmentUpdate();
                    IssueRepository.getInstance().updateField(
                            issue.getId(), type, newStaffAssignment, task -> {
                                if (task.isSuccessful()) {
                                    issue.setAssignedStaff(newStaffAssignment);
                                } else {
                                    Toast.makeText(itemView.getContext(),
                                            "Failed to update Staff Assignment",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    String content = String.format("Issue now assigned from %s to %s",
                            issue.getAssignedStaff(), newStaffAssignment);
                    IssueUpdate staffUpdate = new IssueUpdate(
                            staffName, IssueUpdate.TYPE_STAFF_ASSIGNMENT, content);
                    IssueUpdateRepository.getInstance().addUpdate(
                            issue.getId(), staffUpdate, task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(
                                            itemView.getContext(),
                                            "Failed to reasign Staff",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }
                    );
                }
                Toast.makeText(itemView.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

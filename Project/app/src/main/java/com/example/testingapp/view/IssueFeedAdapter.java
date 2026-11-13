package com.example.testingapp.view;

import android.content.Intent;
import android.widget.Button;
import android.graphics.Color;
import android.view.LayoutInflater;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testingapp.R;
import com.example.testingapp.model.Issue;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying issues in the feed.
 * Features:
 * - Expandable cards that show full details when tapped
 * - Priority bar with color coding (Red = High, Orange = Medium, Green = Low/4)
 * - Warning icon for open/in-progress issues
 */
public class IssueFeedAdapter extends ListAdapter<Issue, IssueFeedAdapter.IssueViewHolder> {

    private int expandedPosition = -1;
    private String currentUserEmail = "";
    public IssueFeedAdapter() {
        super(DIFF_CALLBACK);
    }
    public void setCurrentUserEmail(String email) {
        this.currentUserEmail = email != null ? email : "";
    }

    private static final DiffUtil.ItemCallback<Issue> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Issue>() {
                @Override
                public boolean areItemsTheSame(@NonNull Issue oldItem, @NonNull Issue newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Issue oldItem, @NonNull Issue newItem) {
                    return oldItem.getTitle().equals(newItem.getTitle())
                            && oldItem.getPriority().equals(newItem.getPriority())
                            && oldItem.getStatus().equals(newItem.getStatus())
                            && oldItem.getWatchers() == newItem.getWatchers();
                }
            };

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        holder.bind(getItem(position),
                position == expandedPosition,
                position,
                currentUserEmail,
                pos -> {
                    if (expandedPosition == pos) {
                        expandedPosition = -1;
                    } else {
                        expandedPosition = pos;
                    }
                    notifyDataSetChanged();
                });
    }

    /**
     * ViewHolder for individual issue items.
     * Holds references to all views in the item layout.
     */
    static class IssueViewHolder extends RecyclerView.ViewHolder {
        private TextView issueTitle;
        private TextView issueCategory;
        private TextView issueStatus;
        private TextView issuePriority;
        private View priorityBar;
        private ImageView warningIcon;
        private LinearLayout detailSection;
        private TextView detailLocation;
        private TextView detailDescription;
        private TextView detailTimestamp;
        private TextView detailCreator;
        private Button btnViewDeetails;
        private TextView tvWatchBadge;
        private TextView tvUpvoteBadge;  // ADD THIS




        IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            issueTitle = itemView.findViewById(R.id.issueTitle);
            issueCategory = itemView.findViewById(R.id.issueCategory);
            issueStatus = itemView.findViewById(R.id.issueStatus);
            issuePriority = itemView.findViewById(R.id.issuePriority);
            priorityBar = itemView.findViewById(R.id.priorityBar);
            warningIcon = itemView.findViewById(R.id.warningIcon);
            detailSection = itemView.findViewById(R.id.detailSection);
            detailLocation = itemView.findViewById(R.id.detailLocation);
            detailDescription = itemView.findViewById(R.id.detailDescription);
            detailTimestamp = itemView.findViewById(R.id.detailTimestamp);
            detailCreator = itemView.findViewById(R.id.detailCreator);
            btnViewDeetails = itemView.findViewById(R.id.btnViewDetails);
            tvWatchBadge = itemView.findViewById(R.id.tvWatchBadge);
            tvUpvoteBadge = itemView.findViewById(R.id.tvUpvoteBadge);
        }

        /**
         * Binds issue data to the view holder.
         * Handles setting text, colors, icons, and expandable details.
         * @param issue The issue object to display
         * @param isExpanded Whether the detail section should be shown
         * @param position Position in the list
         * @param listener Click listener for expansion
         * @param userEmail user email
         */
        void bind(Issue issue, boolean isExpanded, int position,
                  String userEmail, OnItemClickListener listener) {
            issueTitle.setText(issue.getTitle());

            // Set category text
            String category = issue.getCategory();
            if (category != null && !category.isEmpty()) {
                issueCategory.setText("Category: " + category);
                issueCategory.setVisibility(View.VISIBLE);
            } else {
                issueCategory.setVisibility(View.GONE);
            }

            issueStatus.setText("Status: " + issue.getStatus());
            issuePriority.setText("Priority: " + issue.getPriority());

            List<String> watchedBy = issue.getWatchedBy();
            boolean userIsWatching = watchedBy != null && watchedBy.contains(userEmail);
            tvWatchBadge.setVisibility(userIsWatching ? View.VISIBLE : View.GONE);

            int upvoteCount = issue.getUpvotes();
            tvUpvoteBadge.setText("↑ " + upvoteCount);

            // Show the badge always (shows count even if 0)
            tvUpvoteBadge.setVisibility(View.VISIBLE);

            // Priority bar colors
            String priority = issue.getPriority();
            if (priority == null) {
                priorityBar.setBackgroundColor(Color.GRAY);
            } else if (priority.contains("1") || priority.equalsIgnoreCase("High")) {
                priorityBar.setBackgroundColor(Color.RED);
            } else if (priority.contains("2") || priority.equalsIgnoreCase("Medium")) {
                priorityBar.setBackgroundColor(Color.parseColor("#FFA500")); // Orange
            } else if (priority.contains("3") || priority.contains("4")
                    || priority.equalsIgnoreCase("Low")) {
                priorityBar.setBackgroundColor(Color.GREEN);
            } else {
                priorityBar.setBackgroundColor(Color.GRAY);
            }

            // Warning Icon for Open/In Progress Issues
            String status = issue.getStatus();
            if (status != null) {
                if (status.equalsIgnoreCase("Closed")) {
                    warningIcon.setVisibility(View.GONE);
                } else {
                    warningIcon.setVisibility(View.VISIBLE);

                    if (status.equalsIgnoreCase("Submitted")) {
                        warningIcon.setColorFilter(Color.parseColor("#076EFF"));
                    } else if (status.equalsIgnoreCase("In Review")) {
                        warningIcon.setColorFilter(Color.parseColor("#FFC107"));
                    } else if (status.equalsIgnoreCase("In Progress")) {
                        warningIcon.setColorFilter(Color.parseColor("#FF9800"));
                    } else if (status.equalsIgnoreCase("Resolved")) {
                        warningIcon.setColorFilter(Color.parseColor("#009688"));
                    }
                }
            } else {
                warningIcon.setVisibility(View.GONE);
            }

            // EXPANDABLE DETAILS
            if (isExpanded) {
                detailSection.setVisibility(View.VISIBLE);
                detailLocation.setText("Location: " + (issue.getLocation()
                        != null ? issue.getLocation() : "N/A"));
                detailDescription.setText("Description: " + (issue.getDescription()
                        != null ? issue.getDescription() : "N/A"));

                if (issue.getTimestamp() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
                    detailTimestamp.setText("Reported: " + sdf.format(issue.getTimestamp()));
                } else {
                    detailTimestamp.setText("Reported: Unknown date");
                }
                // FIXED: Use getUid()
                detailCreator.setText("By: " + (issue.getUid()
                        != null ? issue.getUid() : "Unknown"));
                //Added
                btnViewDeetails.setVisibility(View.VISIBLE);
                btnViewDeetails.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), IssueDetailScreen.class);
                    intent.putExtra(IssueDetailScreen.EXTRA_ISSUE, issue);
                    v.getContext().startActivity(intent);
                });
            } else {
                detailSection.setVisibility(View.GONE);
                //Added to hide button when card is collapsed
                btnViewDeetails.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(position));
        }

        // Interface for click handling - ONLY ONE
        interface OnItemClickListener {
            void onItemClick(int position);
        }
    }
}
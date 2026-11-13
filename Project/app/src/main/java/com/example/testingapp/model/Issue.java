package com.example.testingapp.model;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class Issue implements Serializable {
    private String id;
    private String title;
    private String category;
    private String priority;
    private String location;
    private String description;
    private String status;
    @ServerTimestamp
    private Date timestamp;
    private Date lastUpdated;
    private String uid;
    private String assignedStaff;
    private String staffNote;
    private int upvotes;           // Count = upvotedBy.size()
    private List<String> upvotedBy;
    private List<String> watchedBy;
    private int watchers;

    public enum Status {
        SUBMITTED("Submitted"),
        IN_REVIEW("In Review"),
        IN_PROGRESS("In Progress"),
        RESOLVED("Resolved"),
        CLOSED("Closed");

        private final String currentStatus;

        Status(String status) {
            this.currentStatus = status;
        }

        public String getCurrentStatus() {
            return currentStatus;
        }

        public Status getNext() {
            switch (this) {
            case SUBMITTED:
                return IN_REVIEW;
            case IN_REVIEW:
                return IN_PROGRESS;
            case IN_PROGRESS:
                return RESOLVED;
            case RESOLVED:
                return CLOSED;
            case CLOSED:
                return CLOSED;
            default:
                return SUBMITTED;
            }
        }

        public static Status fromString(String status) {
            if (status == null) {
                return SUBMITTED;
            }
            for (Status s : Status.values()) {
                if (s.currentStatus.equals(status)) {
                    return s;
                }
            }
            return SUBMITTED; // Will fix NullPointerException code smell
        }
    }

    public Issue() {
    }

    public Issue(String title,
                 String category,
                 String priority,
                 String location,
                 String description,
                 String uid) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.location = location;
        this.description = description;
        this.uid = uid;
        this.status = Status.SUBMITTED.getCurrentStatus();
        this.assignedStaff = "None";
        this.lastUpdated = new Date();
        this.upvotes = 0;
        this.upvotedBy = new ArrayList<>();
        this.watchedBy = new ArrayList<>();
        this.watchers = 0;
    }

    public void incrementStatus() {
        Status curr = Status.fromString(status);
        this.status = curr.getNext().getCurrentStatus();
        this.lastUpdated = new Date();
    }

    //getters for Issue class
    @Exclude
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getPriority() {
        return priority;
    }
    public String getLocation() {
        return location;
    }
    public String getDescription() {
        return description;
    }
    public String getUid() {
        return uid;
    }
    public Date getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getAssignedStaff() {
        return assignedStaff;
    }

    public String getStaffNote() {
        return staffNote;
    }

    public int getUpvotes() {
        return upvotes;
    }
    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }
    public List<String> getUpvotedBy() {
        return upvotedBy;
    }
    public void setUpvotedBy(List<String> upvotedBy) {
        this.upvotedBy = upvotedBy;
    }
    public int getWatchers() {
        return watchers;
    }
    public List<String> getWatchedBy() {
        return watchedBy;
    }
    public void setWatchedBy(List<String> watchedBy) {
        this.watchedBy = watchedBy;
    }
    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }


    //setters for Issue class
    public void setId(String id) {
        this.id = id;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = new Date();
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignedStaff(String staff) {
        this.assignedStaff = staff;
    }

    public void setStaffNote(String note) {
        this.staffNote = note;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    // Method for checking if an issue is closed
    public boolean isClosed() {
        return Status.CLOSED.getCurrentStatus().equals(this.status);
    }
}

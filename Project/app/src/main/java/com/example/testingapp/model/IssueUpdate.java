package com.example.testingapp.model;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;


public class IssueUpdate {
    public static final String TYPE_COMMENT = "COMMENT";
    public static final String TYPE_STAFF_NOTE = "STAFF_NOTE";
    public static final String TYPE_STATUS_CHANGE = "STATUS_CHANGE";
    public static final String TYPE_STAFF_ASSIGNMENT = "STAFF_ASSIGNMENT";

    private String author;
    private String type;
    private String content;

    @ServerTimestamp
    private Date timestamp;
    public IssueUpdate() { }
    public IssueUpdate(String author, String type, String content) {
        this.author = author;
        this.type = type;
        this.content = content;
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
package com.example.testingapp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import com.example.testingapp.model.Issue;

public class IssueStatusTest {

    private static final String SUBMITTED = "Submitted";
    private static final String IN_REVIEW = "In Review";
    private static final String IN_PROGRESS = "In Progress";
    private static final String RESOLVED = "Resolved";
    private static final String CLOSED = "Closed";

    @Test
    public void testStatusTransition() {
        Issue issue = new Issue("Title", "Category", "Priority", "Location", "Description", "uid");

        assertEquals(SUBMITTED, issue.getStatus());

        issue.incrementStatus();
        assertEquals(IN_REVIEW, issue.getStatus());

        issue.incrementStatus();
        assertEquals(IN_PROGRESS, issue.getStatus());

        issue.incrementStatus();
        assertEquals(RESOLVED, issue.getStatus());

        issue.incrementStatus();
        assertEquals(CLOSED, issue.getStatus());
    }

    @Test
    public void testClosedTransition() {
        Issue issue = new Issue("Title", "Category", "Priority", "Location", "Description", "uid");

        issue.setStatus(CLOSED);

        issue.incrementStatus();
        assertEquals(CLOSED, issue.getStatus());
    }
}

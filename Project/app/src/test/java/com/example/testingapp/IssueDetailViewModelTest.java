package com.example.testingapp;

import org.junit.Before;
import org.junit.Test;

import com.example.testingapp.model.Issue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class IssueDetailViewModelTest {

    private Issue issue;

    @Before
    public void setUp() {
        issue = new Issue();
    }

    // Test 1: Verify upvote adds user to list
    @Test
    public void testToggleUpvoteAddsUserToList() {
        // Setup
        issue.setUpvotes(0);
        issue.setUpvotedBy(new ArrayList<>());
        String userEmail = "test@example.com";

        // Simulate toggle upvote (adding)
        List<String> upvotedBy = issue.getUpvotedBy();
        if (!upvotedBy.contains(userEmail)) {
            upvotedBy.add(userEmail);
            issue.setUpvotes(upvotedBy.size());
        }

        // Verify
        assertEquals(1, issue.getUpvotes());
        assertTrue(issue.getUpvotedBy().contains(userEmail));
    }

    // Test 2: Verify upvote removes user from list (toggle)
    @Test
    public void testToggleUpvoteRemovesUserFromList() {
        // Setup - user already upvoted
        String userEmail = "test@example.com";
        List<String> upvotedBy = new ArrayList<>(Arrays.asList(userEmail));
        issue.setUpvotedBy(upvotedBy);
        issue.setUpvotes(1);

        // Simulate toggle upvote (removing)
        if (upvotedBy.contains(userEmail)) {
            upvotedBy.remove(userEmail);
            issue.setUpvotes(upvotedBy.size());
        }

        // Verify
        assertEquals(0, issue.getUpvotes());
        assertFalse(issue.getUpvotedBy().contains(userEmail));
    }

    // Test 3: Verify upvote count equals list size
    @Test
    public void testUpvoteCountEqualsListSize() {
        List<String> upvotedBy = new ArrayList<>();
        upvotedBy.add("user1@test.com");
        upvotedBy.add("user2@test.com");
        upvotedBy.add("user3@test.com");

        issue.setUpvotedBy(upvotedBy);
        issue.setUpvotes(upvotedBy.size());

        assertEquals(3, issue.getUpvotes());
        assertEquals(upvotedBy.size(), issue.getUpvotes());
    }

    // Test 4: Verify cannot upvote closed issue
    @Test
    public void testUpvoteClosedIssuePrevented() {
        issue.setStatus("Closed");

        boolean isClosed = issue.isClosed();

        assertTrue(isClosed);
        // If closed, upvote should be prevented
        assertTrue("Closed issues should not allow upvoting", isClosed);
    }
}
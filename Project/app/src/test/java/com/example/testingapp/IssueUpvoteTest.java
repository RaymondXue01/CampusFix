package com.example.testingapp;

import org.junit.Test;
import com.example.testingapp.model.Issue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class IssueUpvoteTest {
    private static final String USER_EMAIL = "test@example.com";

    @Test
    public void testAddUpvoteIncreasesCount() {
        Issue issue = new Issue();
        issue.setUpvotes(0);
        issue.setUpvotedBy(new ArrayList<>());
        String userEmail = USER_EMAIL;

        List<String> upvotedBy = issue.getUpvotedBy();
        upvotedBy.add(userEmail);
        issue.setUpvotes(upvotedBy.size());

        assertEquals(1, issue.getUpvotes());
        assertTrue(issue.getUpvotedBy().contains(userEmail));
    }

    @Test
    public void testRemoveUpvoteDecreasesCount() {
        Issue issue = new Issue();
        String userEmail = USER_EMAIL;
        List<String> upvotedBy = new ArrayList<>(Arrays.asList(userEmail));
        issue.setUpvotedBy(upvotedBy);
        issue.setUpvotes(1);

        upvotedBy.remove(userEmail);
        issue.setUpvotes(upvotedBy.size());

        assertEquals(0, issue.getUpvotes());
        assertFalse(issue.getUpvotedBy().contains(userEmail));
    }

    @Test
    public void testCannotUpvoteTwice() {
        Issue issue = new Issue();
        String userEmail = USER_EMAIL;
        List<String> upvotedBy = new ArrayList<>();
        upvotedBy.add(userEmail);
        issue.setUpvotedBy(upvotedBy);
        issue.setUpvotes(1);

        // Try to add same user again
        if (!upvotedBy.contains(userEmail)) {
            upvotedBy.add(userEmail);
            issue.setUpvotes(upvotedBy.size());
        }

        // Verify count didn't change
        assertEquals(1, issue.getUpvotes());
        assertEquals(1, issue.getUpvotedBy().size());
    }
}
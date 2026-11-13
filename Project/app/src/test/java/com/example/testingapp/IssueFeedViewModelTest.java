package com.example.testingapp;

import com.example.testingapp.model.Issue;
import com.example.testingapp.model.Pair;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IssueFeedViewModelTest {

    private List<Issue> testIssues;
    private static final String CATEGORY_1 = "Maintenance";
    private static final String CATEGORY_2 = "Safety";
    private static final String CATEGORY_3 = "Cleanliness";
    private static final String STATUS_1 = "Open";
    private static final String STATUS_2 = "Closed";
    private static final String STATUS_3 = "In Progress";

    @Before
    public void setUp() {
        testIssues = new ArrayList<>();

        // Test Issue 1: Open - Maintenance
        Issue issue1 = new Issue();
        issue1.setTitle("Broken Light");
        issue1.setStatus(STATUS_1);
        issue1.setCategory(CATEGORY_1);
        testIssues.add(issue1);

        // Test Issue 2: Closed - Safety
        Issue issue2 = new Issue();
        issue2.setTitle("Trip Hazard");
        issue2.setStatus(STATUS_2);
        issue2.setCategory(CATEGORY_2);
        testIssues.add(issue2);

        // Test Issue 3: In Progress - Cleanliness
        Issue issue3 = new Issue();
        issue3.setTitle("Trash Overflow");
        issue3.setStatus(STATUS_3);
        issue3.setCategory(CATEGORY_3);
        testIssues.add(issue3);

        // Test Issue 4: Open - Safety
        Issue issue4 = new Issue();
        issue4.setTitle("Wet Floor");
        issue4.setStatus(STATUS_1);
        issue4.setCategory(CATEGORY_2);
        testIssues.add(issue4);
    }

    @Test
    public void testOpenFilterRemovesClosedIssues() {
        // Simulate "Open Issues" filter (remove Closed status)
        List<Issue> filtered = new ArrayList<>(testIssues);
        filtered.removeIf(issue -> STATUS_2.equals(issue.getStatus()));

        // Should have 3 issues (all except the closed one)
        assertEquals(3, filtered.size());

        // Verify no closed issues remain
        for (Issue issue : filtered) {
            assertNotEquals(STATUS_2, issue.getStatus());
        }
    }

    @Test
    public void testCategoryFilterOnlyShowsSafetyCategory() {
        // Simulate filtering by "Safety" category
        List<Issue> filtered = new ArrayList<>(testIssues);
        filtered.removeIf(issue -> !CATEGORY_2.equals(issue.getCategory()));

        // Should have 2 Safety issues
        assertEquals(2, filtered.size());

        // Verify all are Safety category
        for (Issue issue : filtered) {
            assertEquals(CATEGORY_2, issue.getCategory());
        }
    }
}
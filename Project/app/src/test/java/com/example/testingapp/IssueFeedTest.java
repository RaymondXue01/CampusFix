package com.example.testingapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.testingapp.model.Issue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
public class IssueFeedTest {
    private static final int TIMEOUT = 200;
    private List<Issue> issueList;
    private static final String ISSUE_TITLE = "Broken Bed";

    @Before
    public void setup() {
        issueList = new ArrayList<>();
        Issue openIssue = new Issue(ISSUE_TITLE,"Maintenance","High",
                "Building A", "Light broke","uid012");
        Issue closedIssue = new Issue("Leaky Pipe", "Maintenance", "Low",
                "Building B", "Pipe is leaking", "uid456");
        issueList.add(openIssue);
        issueList.add(closedIssue);
    }

    @Test(timeout = TIMEOUT)
    public void testIssueFieldsSetCorrectly() {
        Issue issue = new Issue("Broken Door", "Safety", "Low",
                "Building C", "Door is cracked", "uid420");

        assertEquals("Broken Door", issue.getTitle());
        assertEquals("Safety", issue.getCategory());
        assertEquals("Low", issue.getPriority());
        assertEquals("Building C", issue.getLocation());
        assertEquals("Submitted", issue.getStatus());
    }

    @Test(timeout = TIMEOUT)
    public void testFilterByOpenStatus() {
        List<Issue> filtered = new ArrayList<>();
        for (Issue issue : issueList) {
            if ("Submitted".equals(issue.getStatus())) {
                filtered.add(issue);
            }
        }

        assertEquals(2, filtered.size());
        assertEquals(ISSUE_TITLE, filtered.get(0).getTitle());
    }

    @Test(timeout = TIMEOUT)
    public void testFilterByPriority() {
        ArrayList<Issue> highPriority = new ArrayList<>();

        for (Issue issue : issueList) {
            if ("High".equals(issue.getPriority())) {
                highPriority.add(issue);
            }
        }

        assertEquals("Number of high priority issues", 1, highPriority.size());
        assertEquals(ISSUE_TITLE, highPriority.get(0).getTitle());
    }

    @Test(timeout = TIMEOUT)
    public void testDescription() {
        issueList.add(new Issue("Toilet Broke", "Plumbing", "2", "Bathroom", "Leaking", "Blah"));
        String description = "Leaking";
        ArrayList<Issue> leaking = new ArrayList<>();

        for (Issue issue : issueList) {
            if (issue.getDescription().contains(description)) {
                leaking.add(issue);
            }
        }

        assertEquals("# of issues with description Leaking", 1, leaking.size());
        assertEquals("Toilet Broke", leaking.get(0).getTitle());
    }

}

package com.example.testingapp;

import com.example.testingapp.model.Issue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class CreateIssueTest {

    private List<Issue> issueList;
    private static final String TITLE_1 = "Test Title";
    private static final String TITLE_2 = "Test Title 2";
    private static final String TITLE_3 = "Test Title 3";
    private static final String CATEGORY_1 = "Category 1";
    private static final String PRIORITY_1 = "Low Priority";
    private static final String PRIORITY_2 = "High Priority";
    private static final String LOCATION_1 = "Location A";
    private static final String LOCATION_2 = "Location C";
    private static final String DESCRIPTION = "Description";

    @Before
    public void setup() {
        issueList = new ArrayList<>();
        Issue openIssue = new Issue("Broken Bed","Maintenance","High",
                "Building A", "Light broke","uid012");
        Issue closedIssue = new Issue("Leaky Pipe", "Maintenance", "Low",
                "Building B", "Pipe is leaking", "uid456");
        issueList.add(openIssue);
        issueList.add(closedIssue);
    }

    @Test
    public void testIssueInitialization() {
        Issue newIssue = new Issue(TITLE_1, "Category", PRIORITY_2, LOCATION_1, DESCRIPTION, "UID");
        assertNotNull(newIssue);
        assertEquals(TITLE_1, newIssue.getTitle());
    }

    @Test
    public void testAddIssueToList() {
        int size = issueList.size();
        Issue newIssue = new Issue("Test New Title", CATEGORY_1, PRIORITY_1, LOCATION_2, DESCRIPTION, "UID");
        issueList.add(newIssue);
        assertEquals(size + 1, issueList.size());
    }
    @Test
    public void testUpdatingIssueAssignedStaff() {
        // Confirm that a new issue starts with "None"
        Issue test = issueList.get(0);
        assertEquals("None", test.getAssignedStaff());
        // Update using setter
        test.setAssignedStaff("New Staff Member");
        // Assert that the Staff Member has changed
        assertNotEquals("None", test.getAssignedStaff());
        assertEquals("New Staff Member", test.getAssignedStaff());
    }
    @Test
    public void testSettingStaffNote() {
        // Assert that an issue does not start with a StaffNote
        Issue test = issueList.get(0);
        assertNull(test.getStaffNote());
        // Create a new staffNote
        String staffNote = "New Staff Note";
        // Assign staffNote to issue
        test.setStaffNote(staffNote);
        // Assert that staffNote has changed
        assertNotNull(test.getStaffNote());
        assertEquals("New Staff Note", test.getStaffNote());
    }

    // Testing if pie chart loads values properly
    @Test
    public void testGraphDataCategoryCounting() {
        // adding to issue list
        issueList.add(new Issue(TITLE_1, CATEGORY_1, PRIORITY_2, LOCATION_1, DESCRIPTION, "UID"));
        issueList.add(new Issue(TITLE_2, "Category 2", PRIORITY_2, LOCATION_1, DESCRIPTION, "UID"));
        issueList.add(new Issue(TITLE_3, CATEGORY_1, PRIORITY_2, LOCATION_2, DESCRIPTION, "UID"));
        issueList.add(new Issue(TITLE_3, CATEGORY_1, PRIORITY_1, LOCATION_2, DESCRIPTION, "UID"));

        // adding variables to look for expected category values
        int actualCategory1 = 0;
        int actualCategory2 = 0;

        // loop through issue list & see if category values added to pie chart match the expected
        for (Issue issue : issueList) {
            if (issue.getCategory().equals(CATEGORY_1)) {
                actualCategory1++;
            } else if (issue.getCategory().equals("Category 2")) {
                actualCategory2++;
            }
        }
        assertEquals(3, actualCategory1);
        assertEquals(1, actualCategory2);
    }

    // Testing empty list
    @Test
    public void testGraphInvalidData() {
        issueList.clear();
        issueList.add(new Issue(TITLE_3, CATEGORY_1, PRIORITY_2, LOCATION_2, DESCRIPTION, "UID"));
        issueList.add(new Issue(TITLE_3, CATEGORY_1, PRIORITY_1, LOCATION_2, DESCRIPTION, "UID"));
        issueList.add(new Issue(TITLE_3, null, PRIORITY_1, LOCATION_2, DESCRIPTION, "UID"));

        // adding variables to look for expected category values
        int actualCategory1 = 0;

        for (Issue issue : issueList) {
            // if issue isn't null & isn't empty
            if (issue.getCategory() != null && !issue.getCategory().isEmpty()) {
                actualCategory1++;
            }
        }

        assertEquals(2, actualCategory1);
    }

    // testing if issue is flagged
    @Test
    public void testIssueFlagged() {
        String flaggedText = "This is very bad word";

        boolean isFlagged = simulateModCheck(flaggedText);

        assertTrue("Content should be flagged", isFlagged);

        if (!isFlagged) {
            issueList.add(new Issue(TITLE_1, CATEGORY_1, PRIORITY_2, LOCATION_2, DESCRIPTION, "UID"));
        }

        assertEquals(2, issueList.size());
    }

    // testing if issue is clean
    // testing if issue is flagged
    @Test
    public void testIssueClean() {
        String cleanText = "There is an issue with something.";

        boolean isFlagged = simulateModCheck(cleanText);

        assertFalse("Content should be clean", isFlagged);

        if (!isFlagged) {
            issueList.add(new Issue(TITLE_1, CATEGORY_1, PRIORITY_2, LOCATION_2, DESCRIPTION, "UID"));
        }

        assertEquals(3, issueList.size());
    }

    // testing issue without calling internet
    private boolean simulateModCheck(String text) {
        return text.toLowerCase().contains("bad");
    }
}

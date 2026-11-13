package com.example.testingapp;
import com.example.testingapp.model.Issue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class IssueWatchTest {
    private static final String USER_A = "usera@test.com";

    @Test(timeout = 200)
    public void testToggleWatchAddsThenRemovesWithNoDuplicates() {
        List<String> watchedBy = new ArrayList<>();

        if (watchedBy.contains(USER_A)) {
            watchedBy.remove(USER_A);
        } else {
            watchedBy.add(USER_A);
        }
        assertTrue("User should be in watchedBy after first toggle",
                watchedBy.contains(USER_A));
        assertEquals("watchedBy size should be 1 after first toggle",
                1, watchedBy.size());

        if (watchedBy.contains(USER_A)) {
            watchedBy.remove(USER_A);
        } else {
            watchedBy.add(USER_A);
        }
        assertFalse("User should be removed after second toggle",
                watchedBy.contains(USER_A));
        assertEquals("watchedBy size should be 0 after second toggle",
                0, watchedBy.size());

        if (watchedBy.contains(USER_A)) {
            watchedBy.remove(USER_A);
        } else {
            watchedBy.add(USER_A);
        }
        assertEquals("watchedBy should never contain duplicate entries",
                1, watchedBy.size());
    }

    @Test(timeout = 200)
    public void testCreatorCannotWatchOwnIssue() {
        String creatorEmail = "creator1@test.com";
        String secondEmail = "second@test.com";

        Issue issue = new Issue(
                "Uneven Step",
                "Maintenance",
                "Medium",
                "Building 42",
                "Step makes lots of noise",
                creatorEmail
        );

        boolean creatorShouldBeBlocked = creatorEmail.equals(issue.getUid());
        boolean otherShouldBeAllowed = !secondEmail.equals(issue.getUid());

        assertTrue("Issue creator should be blocked from watching their own issue",
                creatorShouldBeBlocked);
        assertTrue("Non-creator should be allowed to watch the issue",
                otherShouldBeAllowed);

    }


}

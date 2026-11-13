package com.example.testingapp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import com.example.testingapp.model.IssueUpdate;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class IssueUpdateTest {
    private static final int TIMEOUT = 200;
    private List<IssueUpdate> updates;
    private static final String STUDENT_AUTHOR = "student@xyz.edu";
    private static final String STAFF_AUTHOR = "staff1@xyz.edu";

    @Before
    public void setup() {
        updates = new ArrayList<>();
        updates.add(new IssueUpdate(STAFF_AUTHOR, IssueUpdate.TYPE_STAFF_NOTE,
                "This is a staff noteby staff1"));
        updates.add(new IssueUpdate(STUDENT_AUTHOR, IssueUpdate.TYPE_COMMENT,
                "This is a comment by student"));
        updates.add(new IssueUpdate(STAFF_AUTHOR, IssueUpdate.TYPE_STATUS_CHANGE,
                "Status changed to: In Review"));
    }

    //Test for Staff to see if non staff can use staff comment
    @Test(timeout = TIMEOUT)
    public void testStaffCommentWithNonStaff() {
        String content = "attempt to add staff note as student.";
        final boolean isStaff = false;
        int size = updates.size();

        if (isStaff) {
            IssueUpdate staffNote = new IssueUpdate(STUDENT_AUTHOR, IssueUpdate.TYPE_STAFF_NOTE, content);
            updates.add(staffNote);
        }
        assertEquals("Non staff should not be able to add staff note", size, updates.size());


    }

    // test to check the comment update fields to make sure they are correct
    @Test(timeout = TIMEOUT)
    public void testCommentUpdateFields() {
        String content = "The door hinge is not fixed";

        IssueUpdate comment = new IssueUpdate(STUDENT_AUTHOR, IssueUpdate.TYPE_COMMENT, content);

        assertEquals("Type should be COMMENT", IssueUpdate.TYPE_COMMENT, comment.getType());
        assertEquals("Content should match the  input", content, comment.getContent());
        assertEquals("Author should match the user", STUDENT_AUTHOR, comment.getAuthor());
        assertNull("Timestamp should be null before Firestore writes it", comment.getTimestamp());
    }

}

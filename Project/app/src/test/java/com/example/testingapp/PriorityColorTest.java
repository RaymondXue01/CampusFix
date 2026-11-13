package com.example.testingapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class PriorityColorTest {

    private int getPriorityColorCode(String priority) {
        if (priority == null) {
            return 4; // Gray
        } else if (priority.contains("1") || priority.equalsIgnoreCase("High")) {
            return 1; // Red
        } else if (priority.contains("2") || priority.equalsIgnoreCase("Medium")) {
            return 2; // Orange
        } else if (priority.contains("3") || priority.contains("4") || priority.equalsIgnoreCase("Low")) {
            return 3; // Green
        } else {
            return 4; // Gray
        }
    }

    @Test
    public void testPriority1ReturnsRed() {
        int color = getPriorityColorCode("1");
        assertEquals("Priority 1 should be RED", 1, color);
    }

    @Test
    public void testPriority2ReturnsOrange() {
        int color = getPriorityColorCode("2");
        assertEquals("Priority 2 should be ORANGE", 2, color);
    }

    @Test
    public void testPriority3ReturnsGreen() {
        int color = getPriorityColorCode("3");
        assertEquals("Priority 3 should be GREEN", 3, color);
    }

    @Test
    public void testPriority4ReturnsGreen() {
        int color = getPriorityColorCode("4");
        assertEquals("Priority 4 should be GREEN (lowest)", 3, color);
    }

    @Test
    public void testPriorityHighReturnsRed() {
        int color = getPriorityColorCode("High");
        assertEquals("High priority should be RED", 1, color);
    }

    @Test
    public void testPriorityMediumReturnsOrange() {
        int color = getPriorityColorCode("Medium");
        assertEquals("Medium priority should be ORANGE", 2, color);
    }

    @Test
    public void testPriorityLowReturnsGreen() {
        int color = getPriorityColorCode("Low");
        assertEquals("Low priority should be GREEN", 3, color);
    }

    @Test
    public void testPriorityNullReturnsGray() {
        int color = getPriorityColorCode(null);
        assertEquals("Null priority should be GRAY", 4, color);
    }

    @Test
    public void testPriorityUnknownReturnsGray() {
        int color = getPriorityColorCode("Unknown");
        assertEquals("Unknown priority should be GRAY", 4, color);
    }
}
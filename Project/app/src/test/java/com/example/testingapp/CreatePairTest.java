package com.example.testingapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.testingapp.model.Pair;
import com.example.testingapp.model.PairRegistry;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreatePairTest {
    private static final int TIMEOUT = 200;

    @Test(timeout = TIMEOUT)
    public void test1Initialization() {
        assertEquals(0, PairRegistry.size());
    }

    @Test(timeout = TIMEOUT)
    public void test2CreatePairs() {
        Pair<String, Integer, Boolean> firstPair = new Pair<>("Test", "password".hashCode(), true);
        Pair<String, Integer, Boolean> anotherPair = new Pair<>("Test1", "pass".hashCode(), false);
        PairRegistry.register(firstPair.getVal0(), firstPair);
        PairRegistry.register(anotherPair.getVal0(), anotherPair);
        assertEquals(2, PairRegistry.size());
    }

    @Test(timeout = TIMEOUT)
    public void test3CheckSizeNoAdding() {
        assertFalse(PairRegistry.isEmpty());
        assertEquals(2, PairRegistry.size());
    }

    @Test(timeout = TIMEOUT)
    public void test4CheckUpdatingPair() {
        Pair<String, Integer, Boolean> slightlyDifferent = new Pair<>("Test", "differentPass".hashCode(), true);
        PairRegistry.register(slightlyDifferent.getVal0(), slightlyDifferent);

        assertEquals(2, PairRegistry.size());
    }

    @Test(timeout = TIMEOUT)
    public void test5ClearRegistry() {
        assertFalse(PairRegistry.isEmpty());

        // Clear the registry
        PairRegistry.clear();
        assertTrue(PairRegistry.isEmpty());
        assertEquals(0, Pair.getNum());
    }
}

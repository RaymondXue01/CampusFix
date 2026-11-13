package com.example.testingapp.model;

import java.util.HashMap;
import java.util.Map;

public class PairRegistry {
    private static Map<String, Pair<String, Integer, Boolean>> registry = new HashMap<>();

    public static void register(String email, Pair<String, Integer, Boolean> pair) {
        registry.put(email, pair);
    }

    public static Pair<String, Integer, Boolean> get(String email) {
        return registry.get(email);
    }

    public static boolean contains(String email) {
        return registry.containsKey(email);
    }

    public static int size() {
        return registry.size();
    }

    public static boolean isEmpty() {
        return registry.isEmpty();
    }

    public static void clear() {
        registry = new HashMap<>();
        Pair.resetNum();
    }
}

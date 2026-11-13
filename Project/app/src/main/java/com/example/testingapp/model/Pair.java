package com.example.testingapp.model;



public class Pair<T, K, V> {
    // Individual Pair attributes
    private T val0;
    private K val1;
    private V val2;

    private static int num = 0;

    // Getters
    public T getVal0() {
        return val0;
    }

    public K getVal1() {
        return val1;
    }

    public V getVal2() {
        return val2;
    }

    public static int getNum() {
        return Pair.num;
    }

    public static void resetNum() {
        Pair.num = 0;
    }

    // Methods
    public static void incrementNum() {
        Pair.num += 1;
    }

    // Constructor
    public Pair(T val0, K val1, V val2) {
        this.val0 = val0;
        this.val1 = val1;
        this.val2 = val2;
        Pair.incrementNum();
    }
}

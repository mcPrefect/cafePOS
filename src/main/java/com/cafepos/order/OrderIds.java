package com.cafepos.order;

public final class OrderIds {
    private static long counter = 1000; 
    
    public static long next() {
        return ++counter;
    }
}

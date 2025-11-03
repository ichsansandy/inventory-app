package com.obs.inventory.util;

public class OrderIdGenerator {
    public static String generateOrderId(Long seq) {
        return "O"+seq;
    }
}

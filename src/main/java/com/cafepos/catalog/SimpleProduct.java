package com.cafepos.catalog;

import com.cafepos.common.Money;
import com.cafepos.decorator.Priced;

public final class SimpleProduct implements Product, Priced {
    private final String id;
    private final String name;
    private final Money basePrice;

public SimpleProduct(String id, String name, Money basePrice) {  
    if (id == null || name == null || basePrice == null) {
        throw new IllegalArgumentException("An input cannot be null");
    }
    if (basePrice.compareTo(Money.zero()) < 0) {
        throw new IllegalArgumentException("Base price cannot be negative");
    }

    this.id = id;
    this.name = name;
    this.basePrice = basePrice;
}

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Money basePrice() {
        return basePrice;
    }

    @Override
    public Money price() {
        return basePrice; // For simple products, price = basePrice
    }
}
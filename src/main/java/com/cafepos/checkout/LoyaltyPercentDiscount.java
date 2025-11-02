package com.cafepos.checkout;

import com.cafepos.common.Money;

public final class LoyaltyPercentDiscount implements DiscountPolicy {
    private final int percent;
    public LoyaltyPercentDiscount(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent must be >= 0");
        this.percent = percent;
    }
    @Override public Money discountOf(Money subtotal) {
        return subtotal.multiply(percent).divide(100);
    }
}

package com.cafepos.checkout;

import com.cafepos.common.Money;

public final class FixedCouponDiscount implements DiscountPolicy {
    private final Money amount;
    public FixedCouponDiscount(Money amount) {
        if (amount == null) throw new IllegalArgumentException("amount required");
        this.amount = amount;
    }
    @Override public Money discountOf(Money subtotal) {
        return (amount.compareTo(subtotal) > 0) ? subtotal : amount; // cap at subtotal
    }
}

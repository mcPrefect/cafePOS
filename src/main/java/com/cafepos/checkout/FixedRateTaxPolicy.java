package com.cafepos.checkout;

import com.cafepos.common.Money;

public final class FixedRateTaxPolicy implements TaxPolicy {
    private final int percent;
    public FixedRateTaxPolicy(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent must be >= 0");
        this.percent = percent;
    }
    @Override public Money taxOn(Money amount) {
        return amount.multiply(percent).divide(100);
    }
    public int percent() { return percent; }
}

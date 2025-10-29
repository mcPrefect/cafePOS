package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Money implements Comparable<Money> {
    private final BigDecimal amount;
    
    public static Money of(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Money cannot be negative");
        }
        BigDecimal bigDecimalValue = BigDecimal.valueOf(value);
        return new Money(bigDecimalValue);
    }

    public static Money zero() {
        return of(0.0);
    }

    private Money(BigDecimal a) {
        if (a == null)
            throw new IllegalArgumentException("amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null Money");
        }
        BigDecimal result = this.amount.add(other.amount);
        return new Money(result);
    }

    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null Money");
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.signum() < 0) {
            result = BigDecimal.ZERO; // keep Money non-negative
        }
        return new Money(result);
}

    public Money multiply(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        BigDecimal qtyBig = BigDecimal.valueOf(qty);
        BigDecimal result = this.amount.multiply(qtyBig);
        return new Money(result);
    }

    public Money divide(int divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        BigDecimal result = this.amount.divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP);
        return new Money(result);
    }

    @Override
    public int compareTo(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot compare to null Money");
        }
        return this.amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Money money = (Money) obj;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

}

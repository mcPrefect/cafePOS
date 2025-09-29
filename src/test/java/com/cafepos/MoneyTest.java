package com.cafepos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.cafepos.common.Money;

class MoneyTest {
    
    @Test
    void money_addition() {
        Money money1 = Money.of(2.00);
        Money money2 = Money.of(3.00);
        Money result = money1.add(money2);
        assertEquals(Money.of(5.00), result);
    }
    
    @Test
    void money_multiplication() {
        Money price = Money.of(4.50);
        Money result = price.multiply(3);
        assertEquals(Money.of(13.50), result);
    }
    
    @Test
    void money_division() {
        Money amount = Money.of(10.00);
        Money result = amount.divide(4);
        assertEquals(Money.of(2.50), result);
    }
}
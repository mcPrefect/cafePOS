package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.cafepos.checkout.DiscountPolicy;
import com.cafepos.checkout.FixedCouponDiscount;
import com.cafepos.checkout.FixedRateTaxPolicy;
import com.cafepos.checkout.LoyaltyPercentDiscount;
import com.cafepos.checkout.NoDiscount;
import com.cafepos.checkout.PricingService;
import com.cafepos.common.Money;

class PricingServiceTest {

    @Test
    void noDiscountAppliesTaxOnly() {
        PricingService pricing = new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10));
        var result = pricing.price(Money.of(100));

        assertEquals(Money.of(100), result.subtotal());
        assertEquals(Money.zero(), result.discount());
        assertEquals(Money.of(10), result.tax());
        assertEquals(Money.of(110), result.total());
    }

    @Test
    void loyaltyDiscountApplied() {
        DiscountPolicy loyalty = new LoyaltyPercentDiscount(5);
        PricingService pricing = new PricingService(loyalty, new FixedRateTaxPolicy(10));
        var result = pricing.price(Money.of(100));

        assertEquals(Money.of(5), result.discount());
        assertEquals(Money.of(9.50), result.tax());
        assertEquals(Money.of(104.50), result.total());
    }

    @Test
    void fixedCouponDiscount() {
        DiscountPolicy coupon = new FixedCouponDiscount(Money.of(2));
        PricingService pricing = new PricingService(coupon, new FixedRateTaxPolicy(10));
        var result = pricing.price(Money.of(10));

        assertEquals(Money.of(2), result.discount());
        assertEquals(Money.of(8.80), result.total());
    }
}

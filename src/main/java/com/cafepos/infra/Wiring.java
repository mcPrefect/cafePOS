package com.cafepos.infra;

import com.cafepos.app.CheckoutService;
import com.cafepos.checkout.FixedRateTaxPolicy;
import com.cafepos.checkout.LoyaltyPercentDiscount;
import com.cafepos.checkout.PricingService;
import com.cafepos.domain.OrderRepository;

public final class Wiring {
    public static record Components(
        OrderRepository repo, 
        PricingService pricing, 
        CheckoutService checkout
    ) {}

    public static Components createDefault() {
        OrderRepository repo = new InMemoryOrderRepository();
        
        PricingService pricing = new PricingService(
            new LoyaltyPercentDiscount(5), 
            new FixedRateTaxPolicy(10)
        );
        
        CheckoutService checkout = new CheckoutService(repo, pricing);
        
        return new Components(repo, pricing, checkout);
    }
}
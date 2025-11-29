package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.cafepos.infra.Wiring;

class WiringTest {

    @Test
    void createsAllComponents() {
        var components = Wiring.createDefault();

        assertNotNull(components.repo());
        assertNotNull(components.pricing());
        assertNotNull(components.checkout());
    }

    @Test
    void pricingServiceConfiguredCorrectly() {
        var components = Wiring.createDefault();
        var result = components.pricing().price(com.cafepos.common.Money.of(100));

        // Default config: 5% loyalty discount, 10% tax
        assertEquals(com.cafepos.common.Money.of(5), result.discount());
        assertEquals(com.cafepos.common.Money.of(104.50), result.total());
    }
}

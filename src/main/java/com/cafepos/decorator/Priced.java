package com.cafepos.decorator;

import com.cafepos.common.Money;

/**
 * Interface for products that can calculate their total price.
 * Simple products return their base price.
 * Decorated products return base price plus all surcharges.
 */
public interface Priced {
    /**
     * Returns the total price of this product, including any decorations.
     * @return the total price
     */
    Money price();
}
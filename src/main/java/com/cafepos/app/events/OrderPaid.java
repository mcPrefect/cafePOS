package com.cafepos.app.events;

/**
 * Event fired when an order has been paid.
 * 
 * This represents the FACT that payment was successfully processed.
 * 
 * WHO PUBLISHES: CheckoutService or OrderController after payment
 * WHO MIGHT LISTEN:
 *   - UI (to show payment success)
 *   - Kitchen (to start preparing)
 *   - Accounting (to record revenue)
 *   - Delivery (to prepare for dispatch)
 */
public record OrderPaid(long orderId) implements OrderEvent {
    // Simple record capturing just the essential fact
}
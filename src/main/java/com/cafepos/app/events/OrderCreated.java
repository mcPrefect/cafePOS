package com.cafepos.app.events;

/**
 * Event fired when a new order is created.
 * 
 * This is an IMMUTABLE record - once created, it cannot be changed.
 * It captures the FACT that "at this moment, order X was created".
 * 
 * WHO PUBLISHES: OrderController.createOrder()
 * WHO MIGHT LISTEN: 
 *   - UI (to show confirmation)
 *   - Kitchen (to prepare for incoming order)
 *   - Analytics (to track order volume)
 */
public record OrderCreated(long orderId) implements OrderEvent {
    // That's it! Records automatically give us:
    // - Constructor: new OrderCreated(123)
    // - Getter: event.orderId()
    // - equals/hashCode/toString
    // - Immutability
}
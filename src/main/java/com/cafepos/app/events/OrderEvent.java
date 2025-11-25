package com.cafepos.app.events;

/**
 * Base interface for all order-related events.
 * 
 * This is a sealed interface, which means only specific events
 * (OrderCreated, OrderPaid) can implement it. This gives us
 * type safety and makes it clear what events exist.
 * 
 * WHY: Having a common base type allows us to handle "any order event"
 * generically if needed, while still being able to handle specific
 * event types individually.
 */
public sealed interface OrderEvent 
    permits OrderCreated, OrderPaid {
    // Empty interface - just a marker that says "I'm an order event"
}
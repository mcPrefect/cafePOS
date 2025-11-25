package com.cafepos.app.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Simple in-process event bus for publish-subscribe communication.
 * 
 * WHAT IT DOES:
 * - Components can SUBSCRIBE to event types they care about
 * - Components can PUBLISH events when things happen
 * - EventBus delivers events to all interested subscribers
 * 
 * WHY IT EXISTS:
 * - Decouples publishers from subscribers
 * - One event can trigger many reactions
 * - Easy to add new listeners without changing existing code
 * 
 * HOW IT WORKS:
 * 1. Store a map of "event type" -> "list of handlers"
 * 2. When someone subscribes, add their handler to the list
 * 3. When someone publishes, call all handlers for that type
 */
public final class EventBus {
    
    // Map: Event Class -> List of handlers for that event
    // Example: OrderCreated.class -> [handler1, handler2, handler3]
    private final Map<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    /**
     * Subscribe to an event type.
     * 
     * Example:
     *   eventBus.on(OrderCreated.class, e -> {
     *       System.out.println("Order " + e.orderId() + " created!");
     *   });
     * 
     * @param type  The class of event to listen for (e.g., OrderCreated.class)
     * @param handler  What to do when that event happens
     */
    public <T> void on(Class<T> type, Consumer<T> handler) {
        // Get the list of handlers for this event type (or create new list)
        // Add this new handler to the list
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Publish an event to all subscribers.
     * 
     * Example:
     *   eventBus.emit(new OrderCreated(123));
     * 
     * This will call ALL handlers that subscribed to OrderCreated.
     * 
     * @param event  The event that just happened
     */
    @SuppressWarnings("unchecked")
    public <T> void emit(T event) {
        // Get all handlers for this event's type
        var list = handlers.getOrDefault(event.getClass(), List.of());
        
        // Call each handler with the event
        for (var handler : list) {
            ((Consumer<T>) handler).accept(event);
        }
    }
}
package com.cafepos.app.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

//Simple in-process event bus for publish-subscribe communication.
public final class EventBus {
    
    // Map: Event Class -> List of handlers for that event
    private final Map<Class<?>, List<Consumer<?>>> handlers = new HashMap<>();

    public <T> void on(Class<T> type, Consumer<T> handler) {
        // Get the list of handlers for this event type (or create new list)
        // Add this new handler to the list
        handlers.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

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
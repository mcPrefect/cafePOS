package com.cafepos;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderObserver;
import com.cafepos.payment.CashPayment;

class ObserverPatternTest {
    
    @Test
    void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "Product A", Money.of(2.00));
        var order = new Order(1);
        
        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));
        
        order.addItem(new LineItem(p, 1));
        
        assertTrue(events.contains("itemAdded"), "Observer should be notified of itemAdded event");
    }
    
    @Test
    void observers_notified_on_payment() {
        var p = new SimpleProduct("B", "Product B", Money.of(5.00));
        var order = new Order(2);
        order.addItem(new LineItem(p, 1));
        
        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));
        
        order.pay(new CashPayment());
        
        assertTrue(events.contains("paid"), "Observer should be notified of paid event");
    }
    
    @Test
    void observers_notified_on_ready() {
        var order = new Order(3);
        
        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt));
        
        order.markReady();
        
        assertTrue(events.contains("ready"), "Observer should be notified of ready event");
    }
    
    @Test
    void multiple_observers_all_notified() {
        var p = new SimpleProduct("C", "Product C", Money.of(3.00));
        var order = new Order(4);
        
        final boolean[] observer1Called = {false};
        final boolean[] observer2Called = {false};
        final boolean[] observer3Called = {false};
        
        order.register((o, evt) -> observer1Called[0] = true);
        order.register((o, evt) -> observer2Called[0] = true);
        order.register((o, evt) -> observer3Called[0] = true);
        
        order.addItem(new LineItem(p, 1));
        
        assertTrue(observer1Called[0], "Observer 1 should be notified");
        assertTrue(observer2Called[0], "Observer 2 should be notified");
        assertTrue(observer3Called[0], "Observer 3 should be notified");
    }
    
    @Test
    void observer_receives_correct_order_reference() {
        var p = new SimpleProduct("D", "Product D", Money.of(4.00));
        var order = new Order(5);
        order.addItem(new LineItem(p, 1));
        
        final Order[] receivedOrder = {null};
        order.register((o, evt) -> receivedOrder[0] = o);
        
        order.markReady();
        
        assertSame(order, receivedOrder[0], "Observer should receive the correct order reference");
    }
    
    @Test
    void unregister_observer_stops_notifications() {
        var p = new SimpleProduct("E", "Product E", Money.of(2.50));
        var order = new Order(6);
        
        final int[] callCount = {0};
        OrderObserver observer = (o, evt) -> callCount[0]++;
        
        order.register(observer);
        order.addItem(new LineItem(p, 1)); // Should notify
        assertEquals(1, callCount[0], "Observer should be called once");
        
        order.unregister(observer);
        order.addItem(new LineItem(p, 1)); // Should NOT notify
        assertEquals(1, callCount[0], "Observer should not be called after unregister");
    }
    
    @Test
    void observer_receives_correct_event_type() {
        var p = new SimpleProduct("F", "Product F", Money.of(6.00));
        var order = new Order(8);
        order.addItem(new LineItem(p, 1));
        
        List<String> eventTypes = new ArrayList<>();
        order.register((o, evt) -> eventTypes.add(evt));
        
        order.addItem(new LineItem(p, 1));  // itemAdded
        order.pay(new CashPayment());        // paid
        order.markReady();                   // ready
        
        assertEquals(3, eventTypes.size(), "Should have 3 events");
        assertEquals("itemAdded", eventTypes.get(0));
        assertEquals("paid", eventTypes.get(1));
        assertEquals("ready", eventTypes.get(2));
    }
    
    @Test
    void observer_not_notified_before_registration() {
        var p = new SimpleProduct("G", "Product G", Money.of(1.50));
        var order = new Order(9);
        
        order.addItem(new LineItem(p, 1)); // No observer registered yet
        
        List<String> events = new ArrayList<>();
        order.register((o, evt) -> events.add(evt)); // Register AFTER event
        
        assertTrue(events.isEmpty(), "Observer should not receive events that occurred before registration");
    }
    
    @Test
    void same_observer_not_registered_twice() {
        var p = new SimpleProduct("H", "Product H", Money.of(3.50));
        var order = new Order(10);
        
        final int[] callCount = {0};
        OrderObserver observer = (o, evt) -> callCount[0]++;
        
        order.register(observer);
        order.register(observer); // Try to register same observer again
        
        order.addItem(new LineItem(p, 1));
        
        assertEquals(1, callCount[0], "Observer should only be called once even if registered multiple times");
    }
}
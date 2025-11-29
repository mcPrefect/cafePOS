package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.cafepos.app.events.EventBus;
import com.cafepos.app.events.OrderCreated;
import com.cafepos.app.events.OrderPaid;

class EventBusTest {

    @Test
    void subscriberReceivesEmittedEvent() {
        EventBus bus = new EventBus();
        long[] received = {0};

        bus.on(OrderCreated.class, e -> received[0] = e.orderId());
        bus.emit(new OrderCreated(42));

        assertEquals(42, received[0]);
    }

    @Test
    void multipleSubscribersAllNotified() {
        EventBus bus = new EventBus();
        int[] count = {0};

        bus.on(OrderPaid.class, e -> count[0]++);
        bus.on(OrderPaid.class, e -> count[0]++);
        bus.emit(new OrderPaid(1));

        assertEquals(2, count[0]);
    }

    @Test
    void differentEventTypesRoutedCorrectly() {
        EventBus bus = new EventBus();
        long[] created = {0};
        long[] paid = {0};

        bus.on(OrderCreated.class, e -> created[0] = e.orderId());
        bus.on(OrderPaid.class, e -> paid[0] = e.orderId());

        bus.emit(new OrderCreated(10));
        bus.emit(new OrderPaid(20));

        assertEquals(10, created[0]);
        assertEquals(20, paid[0]);
    }
}

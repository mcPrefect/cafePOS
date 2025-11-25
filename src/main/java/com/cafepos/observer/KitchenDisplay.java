package com.cafepos.observer;

import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderObserver;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if ("itemAdded".equals(eventType)) {
            // Get the last added item
            LineItem lastItem = order.items().get(order.items().size() - 1);
            System.out.println("[Kitchen] Order #" + order.id() + ": " + 
                lastItem.quantity() + "x " + lastItem.product().name() + " added");
        } else if ("paid".equals(eventType)) {
            System.out.println("[Kitchen] Order #" + order.id() + ": payment received");
        }
    }
}

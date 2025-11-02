package com.cafepos.domain;

import com.cafepos.order.Order;

public interface OrderObserver {
    void updated(Order order, String eventType);
}
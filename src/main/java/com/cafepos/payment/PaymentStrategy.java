package com.cafepos.payment;

import com.cafepos.order.Order;

public interface PaymentStrategy {
void pay(Order order);
}

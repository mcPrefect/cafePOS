package com.cafepos.catalog.payment;

import com.cafepos.domain.Order;

public interface PaymentStrategy {
void pay(Order order);
}

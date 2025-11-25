package com.cafepos.demo;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;

public final class Week3Demo {
    public static void main(String[] args) {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
        
        // Cash payment example
        Order order1 = new Order(OrderIds.next());
        order1.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order1.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
        System.out.println("Order #" + order1.id() + " Total: " + order1.totalWithTax(10));
        order1.pay(new CashPayment());
        
        // Card payment example
        Order order2 = new Order(OrderIds.next());
        order2.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order2.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
        System.out.println("Order #" + order2.id() + " Total: " + order2.totalWithTax(10));
        order2.pay(new CardPayment("1234567812341234"));
    }
}

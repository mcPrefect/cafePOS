package com.cafepos.command;

import com.cafepos.catalog.Product;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.PaymentStrategy;

/**
 * OrderService delegates commands to the Order domain object.
 * 
 * Smell fix:
 * Originally used reflection to manipulate Order's private items field:
 *   var field = Order.class.getDeclaredField("items");
 *   field.setAccessible(true);
 *   field.set(order, items);
 * 
 * This broke encapsulation and was fragile.
 * Refactored to use Order's public API instead (see removeLastItem below).
 * Went from 13 lines of reflection → 2 lines using domain API.
 */

public final class OrderService {
    private final ProductFactory factory = new ProductFactory();
    private final Order order;

    public OrderService(Order order) {
        this.order = order;
    }

    public void addItem(String recipe, int qty) {
        Product p = factory.create(recipe);
        order.addItem(new LineItem(p, qty));
        System.out.println("[Service] Added " + p.name() + " x" + qty);
    }

    public void removeLastItem() {
        order.removeLastItem();
        System.out.println("[Service] Removed last item");
    }

    public Money totalWithTax(int percent) {
        return order.totalWithTax(percent);
    }

    public void pay(PaymentStrategy strategy, int taxPercent) {
        // Usually you'd call order.pay(strategy), here we display a payment using the
        // computed total
        var total = order.totalWithTax(taxPercent);
        strategy.pay(order);
        System.out.println("[Service] Payment processed for total " +
                total);
    }

    public Order order() {
        return order;
    }
}
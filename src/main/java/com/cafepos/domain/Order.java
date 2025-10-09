package com.cafepos.domain;

import java.util.ArrayList;
import java.util.List;

import com.cafepos.catalog.payment.PaymentStrategy;
import com.cafepos.common.Money;

public final class Order implements OrderPublisher{
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();
    
    public Order(long id) {
        this.id = id;
    }

    public long id() {
        return id;
    }

    public List<LineItem> items() {
        return items; 
    }

    public void addItem(LineItem li) {
        if (li == null) {
            throw new IllegalArgumentException("LineItem cannot be null");
        }
        items.add(li);
        notifyObservers("itemAdded");
    }   

    public Money subtotal() {
        return items.stream().map(LineItem::lineTotal).reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
    return this.subtotal().multiply(percent).divide(100);
    }

    public Money totalWithTax(int percent) {
        Money total = this.subtotal().add(this.taxAtPercent(percent));
        return total;
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy required");
        }
        strategy.pay(this);
        notifyObservers("paid");
    }

    @Override
    public void register(OrderObserver o) {
        if (o == null) {
            throw new IllegalArgumentException("observer required");
        }
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        for (OrderObserver obs : observers) {
            obs.updated(order, eventType);
        }
    }

    private void notifyObservers(String eventType) {
        notifyObservers(this, eventType);
    }

    public void markReady() {
        notifyObservers("ready");
    }
}

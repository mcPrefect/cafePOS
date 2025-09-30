package com.cafepos;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.cafepos.catalog.SimpleProduct;
import com.cafepos.catalog.payment.CardPayment;
import com.cafepos.catalog.payment.CashPayment;
import com.cafepos.catalog.payment.PaymentStrategy;
import com.cafepos.catalog.payment.WalletPayment;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;

class PaymentStrategyTest {
    
    @Test 
    void payment_strategy_called() {
        var p = new SimpleProduct("A", "A", Money.of(5));
        var order = new Order(42);
        order.addItem(new LineItem(p, 1));
        
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;
        
        order.pay(fake);
        
        assertTrue(called[0], "Payment strategy should be called");
    }
    
    @Test
    void payment_strategy_receives_correct_order() {
        var p = new SimpleProduct("B", "Product B", Money.of(10));
        var order = new Order(100);
        order.addItem(new LineItem(p, 2));
        
        final Order[] receivedOrder = {null};
        PaymentStrategy fake = o -> receivedOrder[0] = o;
        
        order.pay(fake);
        
        assertSame(order, receivedOrder[0], "Strategy should receive the correct order");
    }
    
    @Test
    void cash_payment_strategy_executes() {
        var p = new SimpleProduct("C", "Coffee", Money.of(3.50));
        var order = new Order(200);
        order.addItem(new LineItem(p, 1));
        
        order.pay(new CashPayment());
    }
    
    @Test
    void card_payment_strategy_executes() {
        var p = new SimpleProduct("D", "Donut", Money.of(2.50));
        var order = new Order(300);
        order.addItem(new LineItem(p, 1));
        
        order.pay(new CardPayment("1234567890123456"));
    }
    
    @Test
    void wallet_payment_strategy_executes() {
        var p = new SimpleProduct("E", "Espresso", Money.of(2.50));
        var order = new Order(400);
        order.addItem(new LineItem(p, 1));
        
        order.pay(new WalletPayment("alice-wallet-01"));
    }
}
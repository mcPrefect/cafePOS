package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;

public final class Week4DemoInteractive {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // setup catalog of products available
            Catalog catalog = new InMemoryCatalog();
            catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
            catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
            catalog.add(new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00)));
            
            System.out.println("=== Café POS - Week 4 Observer Pattern Demo ===\n");
            
            // create new order
            Order order = new Order(OrderIds.next());
            System.out.println("Created Order #" + order.id());
            
            // Register observers
            order.register(new KitchenDisplay());
            order.register(new DeliveryDesk());
            order.register(new CustomerNotifier());
            // System.out.println("Registered all observers (Kitchen, Delivery, Customer)\n");
            
            boolean running = true;
            while (running) {
                System.out.println("\n--- Menu Options---");
                System.out.println("1. Add Espresso");
                System.out.println("2. Add Chocolate Cookie");
                System.out.println("3. Add Cappuccino");
                System.out.println("4. Pay (Cash)");
                System.out.println("5. Pay (Card)");
                System.out.println("6. Mark Order Ready");
                System.out.println("7. Show Order Summary");
                System.out.println("8. Exit");
                System.out.print("Choose option: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                
                try {
                    switch (choice) {
                        case 1 -> order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 1));
                        case 2 -> order.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
                        case 3 -> order.addItem(new LineItem(catalog.findById("P-CAP").orElseThrow(), 1));
                        case 4 -> {
                            if (order.items().isEmpty()) {
                                System.out.println("Cannot pay - order is empty!");
                            } else {
                                order.pay(new CashPayment());
                            }
                        }
                        case 5 -> {
                            if (order.items().isEmpty()) {
                                System.out.println("Cannot pay - order is empty!");
                            } else {
                                System.out.print("Enter card number: ");
                                String cardNum = scanner.nextLine();
                                order.pay(new CardPayment(cardNum));
                            }
                        }
                        case 6 -> order.markReady();
                        case 7 -> {
                            System.out.println("\n--- Order Summary ---");
                            System.out.println("Order #" + order.id());
                            System.out.println("Items: " + order.items().size());
                            System.out.println("Subtotal: " + order.subtotal());
                            System.out.println("Tax (10%): " + order.taxAtPercent(10));
                            System.out.println("Total: " + order.totalWithTax(10));
                        }
                        case 8 -> {
                            running = false;
                            System.out.println("Exiting...");
                        }
                        default -> System.out.println("Invalid option!");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}
package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.catalog.payment.CardPayment;
import com.cafepos.catalog.payment.CashPayment;
import com.cafepos.catalog.payment.WalletPayment;
import com.cafepos.common.Money;
import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.SizeLarge;
import com.cafepos.decorator.Syrup;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;

public final class InteractiveDemo {
    private static Scanner scanner;
    private static Catalog catalog;
    private static ProductFactory factory;
    
    // Order states
    private enum OrderState {
        TAKING_ORDER,
        READY_TO_PAY,
        PAID,
        COMPLETED
    }
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        setupCatalog();
        factory = new ProductFactory();
        
        boolean running = true;
        while (running) {
            System.out.println("\n╔════════════ MAIN MENU ═════════════╗");
            System.out.println("║ 1. New Order                       ║");
            System.out.println("║ 2. Exit System                     ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Choose option: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1 -> processNewOrder();
                case 2 -> {
                    running = false;
                    System.out.println("\n✓ Thank you for using Café POS! Goodbye! 👋\n");
                }
                default -> System.out.println("❌ Invalid option!");
            }
        }
        
        scanner.close();
    }
    
    private static void setupCatalog() {
        catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.20)));
        catalog.add(new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00)));
        catalog.add(new SimpleProduct("P-AME", "Americano", Money.of(2.80)));
        catalog.add(new SimpleProduct("P-MOC", "Mocha", Money.of(3.50)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
        catalog.add(new SimpleProduct("P-CRO", "Croissant", Money.of(2.75)));
        catalog.add(new SimpleProduct("P-MUF", "Muffin", Money.of(3.00)));
    }
    
    private static void processNewOrder() {
        Order order = new Order(OrderIds.next());
        OrderState state = OrderState.TAKING_ORDER;
        
        // Register observers
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());
        
        System.out.println("\nCreated Order #" + order.id());
        System.out.println("✓ Observers registered\n");
        
        while (state != OrderState.COMPLETED) {
            switch (state) {
                case TAKING_ORDER -> state = takingOrderMenu(order);
                case READY_TO_PAY -> state = paymentMenu(order);
                case PAID -> state = fulfillmentMenu(order);
            }
        }
    }
    
    private static OrderState takingOrderMenu(Order order) {
        System.out.println("\n╔═════════════ ORDER #" + order.id() + " - TAKING ORDER ══════════════╗");
        System.out.println("║                                                       ║");
        System.out.println("║  DRINKS                      FOOD                     ║");
        System.out.println("║  1. Espresso        2.50     5. Chocolate Cookie 3.50 ║");
        System.out.println("║  2. Latte           3.20     6. Croissant        2.75 ║");
        System.out.println("║  3. Cappuccino      3.00     7. Muffin           3.00 ║");
        System.out.println("║  4. Americano       2.80                              ║");
        System.out.println("║                                                       ║");
        System.out.println("║  8. Use Factory (Recipe Code)                         ║");
        System.out.println("║  9. View Current Order                                ║");
        System.out.println("║ 10. Proceed to Payment                                ║");
        System.out.println("║ 11. Cancel Order                                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        try {
            switch (choice) {
                case 1 -> addCustomizedDrink(order, "P-ESP", "Espresso", Money.of(2.50));
                case 2 -> addCustomizedDrink(order, "P-LAT", "Latte", Money.of(3.20));
                case 3 -> addCustomizedDrink(order, "P-CAP", "Cappuccino", Money.of(3.00));
                case 4 -> addCustomizedDrink(order, "P-AME", "Americano", Money.of(2.80));
                case 5 -> {
                    order.addItem(new LineItem(catalog.findById("P-CCK").orElseThrow(), 1));
                    System.out.println("✓ Added Chocolate Cookie");
                }
                case 6 -> {
                    order.addItem(new LineItem(catalog.findById("P-CRO").orElseThrow(), 1));
                    System.out.println("✓ Added Croissant");
                }
                case 7 -> {
                    order.addItem(new LineItem(catalog.findById("P-MUF").orElseThrow(), 1));
                    System.out.println("✓ Added Muffin");
                }
                case 8 -> addFromFactory(order);
                case 9 -> showOrderSummary(order);
                case 10 -> {
                    if (order.items().isEmpty()) {
                        System.out.println("❌ Cannot proceed - order is empty!");
                        return OrderState.TAKING_ORDER;
                    }
                    return OrderState.READY_TO_PAY;
                }
                case 11 -> {
                    System.out.println("❌ Order cancelled");
                    return OrderState.COMPLETED;
                }
                default -> System.out.println("❌ Invalid option!");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
        
        return OrderState.TAKING_ORDER;
    }
    
    private static void addCustomizedDrink(Order order, String id, String name, Money basePrice) {
        Product drink = new SimpleProduct(id, name, basePrice);
        
        System.out.println("\n--- Customize " + name + " ---");
        System.out.println("Add extras? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y")) {
            System.out.println("\n1. Extra Shot (+0.80)");
            System.out.println("2. Oat Milk (+0.50)");
            System.out.println("3. Syrup (+0.40)");
            System.out.println("4. Large Size (+0.70)");
            System.out.println("0. Done customizing");
            
            boolean customizing = true;
            while (customizing) {
                System.out.print("Add option (0 when done): ");
                int option = getIntInput();
                
                switch (option) {
                    case 1 -> {
                        drink = new ExtraShot(drink);
                        System.out.println("  ✓ Added Extra Shot");
                    }
                    case 2 -> {
                        drink = new OatMilk(drink);
                        System.out.println("  ✓ Added Oat Milk");
                    }
                    case 3 -> {
                        drink = new Syrup(drink);
                        System.out.println("  ✓ Added Syrup");
                    }
                    case 4 -> {
                        drink = new SizeLarge(drink);
                        System.out.println("  ✓ Made Large");
                    }
                    case 0 -> customizing = false;
                    default -> System.out.println("  ❌ Invalid option");
                }
            }
        }
        
        order.addItem(new LineItem(drink, 1));
        System.out.println("✓ Added: " + drink.name());
    }
    
    private static void addFromFactory(Order order) {
        System.out.println("\n--- Factory Recipe Builder ---");
        System.out.println("Base codes: ESP, LAT, CAP, AME");
        System.out.println("Add-ons: SHOT (extra shot), OAT (oat milk), SYP (syrup), L (large)");
        System.out.println("Example: ESP+SHOT+OAT+L");
        System.out.print("Enter recipe: ");
        
        String recipe = scanner.nextLine().trim();
        
        try {
            Product product = factory.create(recipe);
            order.addItem(new LineItem(product, 1));
            System.out.println("✓ Added: " + product.name());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid recipe: " + e.getMessage());
        }
    }
    
    private static OrderState paymentMenu(Order order) {
        showOrderSummary(order);
        
        System.out.println("\n╔════════════ PAYMENT ═══════════════╗");
        System.out.println("║ 1. Pay with Cash                   ║");
        System.out.println("║ 2. Pay with Card                   ║");
        System.out.println("║ 3. Pay with Digital Wallet         ║");
        System.out.println("║ 4. Back to Order (add more items)  ║");
        System.out.println("║ 5. Cancel Order                    ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose payment method: ");
        
        int choice = getIntInput();
        
        try {
            switch (choice) {
                case 1 -> {
                    System.out.println("\n Processing cash payment...");
                    order.pay(new CashPayment());
                    System.out.println("✓ Payment successful!");
                    return OrderState.PAID;
                }
                case 2 -> {
                    System.out.print("Enter card number: ");
                    String cardNum = scanner.nextLine().trim();
                    System.out.println("\n Processing card payment...");
                    order.pay(new CardPayment(cardNum));
                    System.out.println("✓ Payment successful!");
                    return OrderState.PAID;
                }
                case 3 -> {
                    System.out.print("Enter wallet ID: ");
                    String walletId = scanner.nextLine().trim();
                    System.out.println("\n Processing wallet payment...");
                    order.pay(new WalletPayment(walletId));
                    System.out.println("✓ Payment successful!");
                    return OrderState.PAID;
                }
                case 4 -> {
                    return OrderState.TAKING_ORDER;
                }
                case 5 -> {
                    System.out.println("❌ Order cancelled");
                    return OrderState.COMPLETED;
                }
                default -> {
                    System.out.println("❌ Invalid option!");
                    return OrderState.READY_TO_PAY;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Payment failed: " + e.getMessage());
            return OrderState.READY_TO_PAY;
        }
    }
    
    private static OrderState fulfillmentMenu(Order order) {
        System.out.println("\n╔════════════ FULFILLMENT ═══════════╗");
        System.out.println("║ Order #" + order.id() + " has been paid          ║");
        System.out.println("║                                    ║");
        System.out.println("║ 1. Mark Order Ready (for pickup)   ║");
        System.out.println("║ 2. View Receipt                    ║");
        System.out.println("║ 3. Complete Order                  ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1 -> {
                System.out.println("\n Marking order ready...");
                order.markReady();
                System.out.println("✓ Order ready for pickup/delivery!");
            }
            case 2 -> showReceipt(order);
            case 3 -> {
                System.out.println("\n✓ Order #" + order.id() + " completed!");
                return OrderState.COMPLETED;
            }
            default -> System.out.println(" Invalid option!");
        }
        
        return OrderState.PAID;
    }
    
    private static void showOrderSummary(Order order) {
        System.out.println("\n╔════════════ ORDER SUMMARY ═════════════╗");
        System.out.println("  Order #" + order.id());
        System.out.println("  ─────────────────────────────────────");
        
        if (order.items().isEmpty()) {
            System.out.println("  (No items yet)");
        } else {
            for (LineItem li : order.items()) {
                System.out.printf("  %-30s x%d%n", li.product().name(), li.quantity());
                System.out.printf("  %40s%n", li.lineTotal() + " EUR");
            }
        }
        
        System.out.println("  ─────────────────────────────────────");
        System.out.println("  Subtotal: " + order.subtotal() + " EUR");
        System.out.println("  Tax (10%): " + order.taxAtPercent(10) + " EUR");
        System.out.println("  TOTAL: " + order.totalWithTax(10) + " EUR");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    private static void showReceipt(Order order) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║            CAFÉ RECEIPT                ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("  Order #" + order.id());
        System.out.println("  ────────────────────────────────────");
        
        for (LineItem li : order.items()) {
            System.out.printf("  %-28s x%d%n", li.product().name(), li.quantity());
            System.out.printf("  %38s%n", li.lineTotal() + " EUR");
        }
        
        System.out.println("  ────────────────────────────────────");
        System.out.println("  Subtotal:        " + order.subtotal() + " EUR");
        System.out.println("  Tax (10%):       " + order.taxAtPercent(10) + " EUR");
        System.out.println("  ────────────────────────────────────");
        System.out.println("  TOTAL:           " + order.totalWithTax(10) + " EUR");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║   Thank you for your order!            ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
    
    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter a number: ");
            }
        }
    }
}
package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.OrderService;
import com.cafepos.command.PayOrderCommand;
import com.cafepos.command.PosRemote;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;

public final class Week8Demo_CLI {
    
    public static void main(String[] args) {
        // Setup
        try (Scanner scanner = new Scanner(System.in)) {
            // Setup
            Order order = new Order(OrderIds.next());
            OrderService service = new OrderService(order);
            PosRemote remote = new PosRemote(5);
            
            // Pre-configure some buttons
            remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
            remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
            remote.setSlot(2, new AddItemCommand(service, "CAP+SHOT+OAT", 1));
            remote.setSlot(3, new PayOrderCommand(service, new CashPayment(), 10));
            remote.setSlot(4, new PayOrderCommand(service, new CardPayment("1234567890123456"), 10));
            
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   CAFÉ POS - Command Pattern Demo     ║");
            System.out.println("║          Interactive CLI               ║");
            System.out.println("╚════════════════════════════════════════╝\n");
            
            System.out.println("Order #" + order.id() + " created\n");
            
            boolean running = true;
            while (running) {
                System.out.println("┌─────────────────────────────┐");
                System.out.println("│  Available Button Actions   │");
                System.out.println("├─────────────────────────────┤");
                System.out.println("│ [0] Add Espresso            │");
                System.out.println("│ [1] Add 2x Large Latte      │");
                System.out.println("│ [2] Add Cappuccino Special  │");
                System.out.println("│ [3] Pay with Cash           │");
                System.out.println("│ [4] Pay with Card           │");
                System.out.println("│ [U] Undo Last Action        │");
                System.out.println("│ [S] Show Order Summary      │");
                System.out.println("│ [Q] Quit                    │");
                System.out.println("└─────────────────────────────┘");
                System.out.print("Press button: ");
                
                String input = scanner.nextLine().trim().toUpperCase();
                System.out.println();
                
                switch (input) {
                    case "0", "1", "2", "3", "4" -> {
                        int button = Integer.parseInt(input);
                        System.out.println("→ Pressing button " + button + "...");
                        remote.press(button);
                        System.out.println("✓ Done\n");
                    }
                    case "U" -> {
                        System.out.println("→ Undoing last action...");
                        remote.undo();
                        System.out.println();
                    }
                    case "S" -> showOrderSummary(order);
                    case "Q" -> {
                        System.out.println("Thanks for using Café POS!");
                        running = false;
                    }
                    default -> System.out.println("❌ Invalid input. Try again.\n");
                }
            }
        }
    }
    
    private static void showOrderSummary(Order order) {
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║      ORDER SUMMARY             ║");
        System.out.println("╠════════════════════════════════╣");
        System.out.println("  Order #" + order.id());
        System.out.println("  ─────────────────────────────");
        
        if (order.items().isEmpty()) {
            System.out.println("  (No items yet)");
        } else {
            int i = 1;
            for (var item : order.items()) {
                System.out.printf("  %d. %s x%d%n", i++, item.product().name(), item.quantity());
                System.out.printf("     %s%n", item.lineTotal() + " EUR");
            }
        }
        
        System.out.println("  ─────────────────────────────");
        System.out.println("  Subtotal: " + order.subtotal() + " EUR");
        System.out.println("  Tax (10%): " + order.taxAtPercent(10) + " EUR");
        System.out.println("  ─────────────────────────────");
        System.out.println("  TOTAL: " + order.totalWithTax(10) + " EUR");
        System.out.println("╚════════════════════════════════╝\n");
    }
}

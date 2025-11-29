package com.cafepos.demo;

import java.util.Scanner;

import com.cafepos.app.CheckoutService;
import com.cafepos.app.events.EventBus;
import com.cafepos.app.events.OrderCreated;
import com.cafepos.app.events.OrderPaid;
import com.cafepos.command.AddItemCommand;
import com.cafepos.command.Command;
import com.cafepos.command.OrderService;
import com.cafepos.command.PayOrderCommand;
import com.cafepos.command.PosRemote;
import com.cafepos.common.Money;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.domain.OrderRepository;
import com.cafepos.checkout.PricingService;
import com.cafepos.infra.Wiring;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import com.cafepos.state.OrderFSM;
import com.cafepos.ui.ConsoleView;

public final class LayeredInteractiveDemo {

    private static Wiring.Components components;
    private static OrderRepository orderRepo;
    private static PricingService pricingService;
    private static CheckoutService checkoutService;
    private static EventBus eventBus;
    private static ConsoleView view;
    private static Scanner scanner;
    private static PosRemote remote;

    private static final int SLOT_ADD_ITEM = 0;
    private static final int SLOT_PAY = 1;
    private static final int TAX_PERCENT = 10;

    public static void main(String[] args) {
        initializeSystem();
        runMainLoop();
        scanner.close();
    }

    private static void initializeSystem() {
        components = Wiring.createDefault();
        orderRepo = components.repo();
        pricingService = components.pricing();
        checkoutService = components.checkout();

        eventBus = new EventBus();
        eventBus.on(OrderCreated.class, e ->
            view.print("[EventBus] OrderCreated: Order #" + e.orderId()));
        eventBus.on(OrderPaid.class, e ->
            view.print("[EventBus] OrderPaid: Order #" + e.orderId()));

        view = new ConsoleView();
        scanner = new Scanner(System.in);
        remote = new PosRemote(5);

        view.print("\n=== CAFE POS (Layered Architecture) ===\n");
    }

    private static void runMainLoop() {
        boolean running = true;
        while (running) {
            view.print("\n╔════════════ MAIN MENU ═════════════╗");
            view.print("║ 1. New Order                       ║");
            view.print("║ 2. Exit System                     ║");
            view.print("╚════════════════════════════════════╝");
            System.out.print("Choose option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> processNewOrder();
                case 2 -> {
                    running = false;
                    view.print("\n✓ Thank you for using Café POS! Goodbye!\n");
                }
                default -> view.print("❌ Invalid option!");
            }
        }
    }

    private static void processNewOrder() {
        long orderId = OrderIds.next();
        Order order = new Order(orderId);
        orderRepo.save(order);

        OrderFSM fsm = new OrderFSM();

        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        OrderService orderService = new OrderService(order);

        eventBus.emit(new OrderCreated(orderId));

        view.print("\nCreated Order #" + orderId);
        view.print("✓ Observers registered");
        view.print("[FSM] State: " + fsm.status() + "\n");

        boolean orderComplete = false;
        while (!orderComplete) {
            orderComplete = switch (fsm.status()) {
                case "NEW" -> handleNewState(order, orderService, fsm);
                case "PREPARING" -> handlePreparingState(order, fsm);
                case "READY" -> handleReadyState(order, fsm);
                case "DELIVERED", "CANCELLED" -> true;
                default -> true;
            };
        }

        view.print("\n[FSM] Final State: " + fsm.status());
    }

    private static boolean handleNewState(Order order, OrderService orderService, OrderFSM fsm) {
        view.print("\n╔═══════════ ORDER #" + order.id() + " - TAKING ORDER ════════════╗");
        view.print("║                                                       ║");
        view.print("║  Recipe codes: ESP, LAT, CAP, AME                     ║");
        view.print("║  Add-ons: SHOT, OAT, SYP, L   (e.g. LAT+L+SHOT)       ║");
        view.print("║                                                       ║");
        view.print("║  1. Add Item          4. Proceed to Payment           ║");
        view.print("║  2. Undo Last Item    5. Cancel Order                 ║");
        view.print("║  3. View Order                                        ║");
        view.print("╚═══════════════════════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter recipe: ");
                String recipe = scanner.nextLine().trim().toUpperCase();
                System.out.print("Quantity: ");
                int qty = getIntInput();

                try {
                    Command addCmd = new AddItemCommand(orderService, recipe, qty);
                    remote.setSlot(SLOT_ADD_ITEM, addCmd);
                    remote.press(SLOT_ADD_ITEM);
                    orderRepo.save(order);
                } catch (Exception e) {
                    view.print("❌ Error: " + e.getMessage());
                }
            }
            case 2 -> {
                remote.undo();
                orderRepo.save(order);
            }
            case 3 -> showOrderSummary(order);
            case 4 -> {
                if (order.items().isEmpty()) {
                    view.print("❌ Cannot proceed - order is empty!");
                    return false;
                }
                return handlePayment(order, orderService, fsm);
            }
            case 5 -> {
                fsm.cancel();
                view.print("❌ Order cancelled");
                return true;
            }
            default -> view.print("❌ Invalid option!");
        }

        return false;
    }

    private static boolean handlePayment(Order order, OrderService orderService, OrderFSM fsm) {
        showOrderSummary(order);

        var pricing = pricingService.price(order.subtotal());
        view.print("\n--- Pricing ---");
        view.print("Subtotal:  " + pricing.subtotal());
        if (pricing.discount().compareTo(Money.zero()) > 0) {
            view.print("Discount:  -" + pricing.discount() + " (5% Loyalty)");
        }
        view.print("Tax (10%): " + pricing.tax());
        view.print("TOTAL:     " + pricing.total());

        view.print("\n╔════════════ PAYMENT ═══════════════╗");
        view.print("║ 1. Pay with Cash                   ║");
        view.print("║ 2. Pay with Card                   ║");
        view.print("║ 3. Pay with Digital Wallet         ║");
        view.print("║ 4. Back to Order                   ║");
        view.print("╚════════════════════════════════════╝");
        System.out.print("Choose payment method: ");

        int choice = getIntInput();
        PaymentStrategy strategy = switch (choice) {
            case 1 -> new CashPayment();
            case 2 -> {
                System.out.print("Enter card number: ");
                yield new CardPayment(scanner.nextLine().trim());
            }
            case 3 -> {
                System.out.print("Enter wallet ID: ");
                yield new WalletPayment(scanner.nextLine().trim());
            }
            default -> null;
        };

        if (strategy == null) {
            if (choice != 4) view.print("❌ Invalid option!");
            return false;
        }

        try {
            Command payCmd = new PayOrderCommand(orderService, strategy, TAX_PERCENT);
            remote.setSlot(SLOT_PAY, payCmd);
            remote.press(SLOT_PAY);

            fsm.pay();
            view.print("✓ Payment successful!");
            view.print("[FSM] State: " + fsm.status());

            eventBus.emit(new OrderPaid(order.id()));
            orderRepo.save(order);
            return false;
        } catch (Exception e) {
            view.print("❌ Payment failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean handlePreparingState(Order order, OrderFSM fsm) {
        view.print("\n╔════════════ FULFILLMENT ═══════════╗");
        view.print("║ Order #" + order.id() + " - " + fsm.status());
        view.print("║                                    ║");
        view.print("║ 1. Mark Order Ready                ║");
        view.print("║ 2. View Receipt                    ║");
        view.print("║ 3. Cancel Order                    ║");
        view.print("╚════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                fsm.markReady();
                order.markReady();
                view.print("✓ Order ready for pickup!");
                view.print("[FSM] State: " + fsm.status());
            }
            case 2 -> showReceipt(order);
            case 3 -> {
                fsm.cancel();
                view.print("❌ Order cancelled");
                return true;
            }
            default -> view.print("❌ Invalid option!");
        }

        return false;
    }

    private static boolean handleReadyState(Order order, OrderFSM fsm) {
        view.print("\n╔════════════ READY FOR PICKUP ══════╗");
        view.print("║ Order #" + order.id() + " is READY!              ║");
        view.print("║                                    ║");
        view.print("║ 1. Deliver to Customer             ║");
        view.print("║ 2. View Receipt                    ║");
        view.print("╚════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                fsm.deliver();
                view.print("\n✓ Order #" + order.id() + " delivered!");
                view.print("[FSM] State: " + fsm.status());
                return true;
            }
            case 2 -> showReceipt(order);
            default -> view.print("❌ Invalid option!");
        }

        return false;
    }

    private static void showOrderSummary(Order order) {
        view.print("\n╔════════════ ORDER SUMMARY ═════════════╗");
        view.print("  Order #" + order.id());
        view.print("  ─────────────────────────────────────");

        if (order.items().isEmpty()) {
            view.print("  (No items yet)");
        } else {
            for (LineItem li : order.items()) {
                view.print(String.format("  %-30s x%d", li.product().name(), li.quantity()));
                view.print(String.format("  %40s", li.lineTotal() + " EUR"));
            }
        }

        view.print("  ─────────────────────────────────────");
        view.print("  Subtotal: " + order.subtotal() + " EUR");
        view.print("╚════════════════════════════════════════╝");
    }

    private static void showReceipt(Order order) {
        view.print("\n╔════════════════════════════════════════╗");
        view.print("║            CAFÉ RECEIPT                ║");
        view.print("╠════════════════════════════════════════╣");
        view.print(checkoutService.checkout(order.id(), TAX_PERCENT));
        view.print("╠════════════════════════════════════════╣");
        view.print("║   Thank you for your order!            ║");
        view.print("╚════════════════════════════════════════╝");
    }

    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a number: ");
            }
        }
    }
}

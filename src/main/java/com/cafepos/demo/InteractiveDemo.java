package com.cafepos.demo;

import java.math.BigDecimal;
import java.util.Scanner;

import com.cafepos.app.CheckoutService;
import com.cafepos.app.events.EventBus;
import com.cafepos.app.events.OrderCreated;
import com.cafepos.app.events.OrderPaid;
import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.checkout.PricingService;
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
import com.cafepos.infra.Wiring;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuComponent;
import com.cafepos.menu.MenuItem;
import com.cafepos.observer.CustomerNotifier;
import com.cafepos.observer.DeliveryDesk;
import com.cafepos.observer.KitchenDisplay;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.WalletPayment;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;
import com.cafepos.state.OrderFSM;
import com.cafepos.ui.ConsoleView;
import com.cafepos.ui.OrderController;

import vendor.legacy.LegacyThermalPrinter;

public final class InteractiveDemo {

    // Infrastructure (Wiring DI)
    private static Wiring.Components components;
    private static OrderRepository orderRepo;
    private static PricingService pricingService;
    private static CheckoutService checkoutService;

    // EventBus (Pub-Sub)
    private static EventBus eventBus;

    // MVC - View and Controller
    private static ConsoleView view;
    private static OrderController controller;

    // Command Pattern
    private static PosRemote remote;

    // Adapter Pattern
    private static Printer receiptPrinter;

    // Composite Pattern (Menu tree)
    private static Menu cafeMenu;

    private static Scanner scanner;
    private static Catalog catalog;

    private static final int SLOT_ADD_ITEM = 0;
    private static final int SLOT_PAY = 1;
    private static final int TAX_PERCENT = 10;

    public static void main(String[] args) {
        initializeSystem();
        runMainLoop();
        scanner.close();
    }

    private static void initializeSystem() {
        // Wiring (DI Container)
        components = Wiring.createDefault();
        orderRepo = components.repo();
        pricingService = components.pricing();
        checkoutService = components.checkout();

        // EventBus setup
        eventBus = new EventBus();

        // MVC - View
        view = new ConsoleView();

        // Subscribe to events (EventBus pattern)
        eventBus.on(OrderCreated.class, e ->
            view.print("[EventBus] OrderCreated: Order #" + e.orderId()));
        eventBus.on(OrderPaid.class, e ->
            view.print("[EventBus] OrderPaid: Order #" + e.orderId()));

        scanner = new Scanner(System.in);

        // Command Pattern - PosRemote
        remote = new PosRemote(5);

        // MVC - Controller
        controller = new OrderController(orderRepo, checkoutService);

        // Adapter Pattern - wrap legacy printer
        receiptPrinter = new LegacyPrinterAdapter(new LegacyThermalPrinter());

        // Composite Pattern - build menu tree
        setupMenuComposite();

        setupCatalog();

        view.print("\n╔════════════════════════════════════════╗");
        view.print("║                                        ║");
        view.print("║         CAFÉ POS SYSTEM                ║");
        view.print("║                                        ║");
        view.print("╚════════════════════════════════════════╝");
        view.print("[MVC] View: ConsoleView, Controller: OrderController\n");
    }

    private static void setupMenuComposite() {
        // Composite Pattern: Menu tree with submenus
        cafeMenu = new Menu("Cafe Menu");

        Menu drinks = new Menu("Hot Drinks");
        drinks.add(new MenuItem("Espresso", BigDecimal.valueOf(2.50), true));
        drinks.add(new MenuItem("Latte", BigDecimal.valueOf(3.20), true));
        drinks.add(new MenuItem("Cappuccino", BigDecimal.valueOf(3.00), true));
        drinks.add(new MenuItem("Americano", BigDecimal.valueOf(2.80), true));

        Menu food = new Menu("Food");
        food.add(new MenuItem("Chocolate Cookie", BigDecimal.valueOf(3.50), true));
        food.add(new MenuItem("Croissant", BigDecimal.valueOf(2.75), true));
        food.add(new MenuItem("Muffin", BigDecimal.valueOf(3.00), true));

        Menu addons = new Menu("Customizations");
        addons.add(new MenuItem("Extra Shot", BigDecimal.valueOf(0.80), true));
        addons.add(new MenuItem("Oat Milk", BigDecimal.valueOf(0.50), true));
        addons.add(new MenuItem("Syrup", BigDecimal.valueOf(0.40), true));
        addons.add(new MenuItem("Large Size", BigDecimal.valueOf(0.70), true));

        cafeMenu.add(drinks);
        cafeMenu.add(food);
        cafeMenu.add(addons);
    }

    private static void setupCatalog() {
        catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.20)));
        catalog.add(new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00)));
        catalog.add(new SimpleProduct("P-AME", "Americano", Money.of(2.80)));
        catalog.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
        catalog.add(new SimpleProduct("P-CRO", "Croissant", Money.of(2.75)));
        catalog.add(new SimpleProduct("P-MUF", "Muffin", Money.of(3.00)));
    }

    private static void showMenuComposite() {
        // Composite + Iterator: traverse menu tree
        view.print("\n╔═══════════════ MENU ══════════════════════╗");
        view.print("║  (Using Composite + Iterator patterns)    ║");
        view.print("╚═══════════════════════════════════════════╝");
        view.print("[Composite] Menu tree: Cafe Menu -> Hot Drinks, Food, Customizations");
        view.print("[Iterator] Traversing menu tree using for-each loop\n");

        // Iterator Pattern: iterate through composite tree
        for (MenuComponent component : cafeMenu) {
            switch (component) {
                case MenuItem item -> view.print("    " + item.name() + " ... " + item.price());
                case Menu menu -> view.print("\n  " + menu.name().toUpperCase());
                default -> {
                }
            }
        }

        view.print("\n[Iterator] Filtering vegetarian items only:");
        cafeMenu.vegetarianItems().forEach(item ->
            view.print("  (v) " + item.name()));

        view.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void runMainLoop() {
        boolean running = true;
        while (running) {
            view.print("\n╔════════════ MAIN MENU ═════════════╗");
            view.print("║ 1. New Order                       ║");
            view.print("║ 2. View Menu (Composite/Iterator)  ║");
            view.print("║ 3. Exit System                     ║");
            view.print("╚════════════════════════════════════╝");
            System.out.print("Choose option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1 -> processNewOrder();
                case 2 -> showMenuComposite();
                case 3 -> {
                    running = false;
                    view.print("\n✓ Thank you for using Café POS! Goodbye! ☕\n");
                }
                default -> view.print("❌ Invalid option!");
            }
        }
    }

    private static void processNewOrder() {
        long orderId = OrderIds.next();
        Order order = new Order(orderId);
        orderRepo.save(order);

        // State Pattern: OrderFSM
        OrderFSM fsm = new OrderFSM();

        // Observer Pattern: register observers to receive order updates
        view.print("[Observer] Registering KitchenDisplay, DeliveryDesk, CustomerNotifier");
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        OrderService orderService = new OrderService(order);

        // EventBus: emit event
        eventBus.emit(new OrderCreated(orderId));

        view.print("\n Created Order #" + orderId);
        view.print("[FSM] State: " + fsm.status());

        boolean orderComplete = false;
        while (!orderComplete) {
            // State Pattern: behavior depends on current state
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
        view.print("\n╔═════════════ ORDER #" + order.id() + " - TAKING ORDER ══════════════╗");
        view.print("║                                                       ║");
        view.print("║  DRINKS                      FOOD                     ║");
        view.print("║  1. Espresso        2.50     5. Chocolate Cookie 3.50 ║");
        view.print("║  2. Latte           3.20     6. Croissant        2.75 ║");
        view.print("║  3. Cappuccino      3.00     7. Muffin           3.00 ║");
        view.print("║  4. Americano       2.80                              ║");
        view.print("║                                                       ║");
        view.print("║  8. Use Recipe Code (Command Pattern)                 ║");
        view.print("║  9. View Current Order                                ║");
        view.print("║ 10. Undo Last Item (Command Pattern)                  ║");
        view.print("║ 11. Proceed to Payment                                ║");
        view.print("║ 12. Cancel Order                                      ║");
        view.print("╚═══════════════════════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1 -> addCustomizedDrink(order, orderService, "ESP", "Espresso");
                case 2 -> addCustomizedDrink(order, orderService, "LAT", "Latte");
                case 3 -> addCustomizedDrink(order, orderService, "CAP", "Cappuccino");
                case 4 -> addCustomizedDrink(order, orderService, "AME", "Americano");
                case 5 -> addItemViaCommand(order, orderService, "CCK", "Chocolate Cookie");
                case 6 -> addItemViaCommand(order, orderService, "CRO", "Croissant");
                case 7 -> addItemViaCommand(order, orderService, "MUF", "Muffin");
                case 8 -> addFromRecipe(order, orderService);
                case 9 -> showOrderSummary(order);
                case 10 -> {
                    // Command Pattern: undo
                    remote.undo();
                    orderRepo.save(order);
                }
                case 11 -> {
                    if (order.items().isEmpty()) {
                        view.print("❌ Cannot proceed - order is empty!");
                        return false;
                    }
                    return handlePayment(order, orderService, fsm);
                }
                case 12 -> {
                    System.out.print("⚠️  Are you sure you want to cancel? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        fsm.cancel();
                        view.print("❌ Order cancelled");
                        return true;
                    }
                }
                default -> view.print("❌ Invalid option!");
            }
        } catch (Exception e) {
            view.print("❌ Error: " + e.getMessage());
        }

        return false;
    }

    private static void addItemViaCommand(Order order, OrderService orderService, String recipeCode, String displayName) {
        // Command Pattern: create and execute command for simple items
        Command addCmd = new AddItemCommand(orderService, recipeCode, 1);
        remote.setSlot(SLOT_ADD_ITEM, addCmd);
        remote.press(SLOT_ADD_ITEM);
        orderRepo.save(order);
        view.print("✓ Added " + displayName);
    }

    private static void addCustomizedDrink(Order order, OrderService orderService, String baseCode, String name) {
        // Decorator Pattern: build recipe string with add-ons
        StringBuilder recipe = new StringBuilder(baseCode);

        view.print("\n--- Customize " + name + " (Decorator Pattern) ---");
        System.out.print("Add extras? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            view.print("\n1. Extra Shot (+0.80)");
            view.print("2. Oat Milk (+0.50)");
            view.print("3. Syrup (+0.40)");
            view.print("4. Large Size (+0.70)");
            view.print("0. Done customizing");

            boolean customizing = true;
            while (customizing) {
                System.out.print("Add option (0 when done): ");
                int option = getIntInput();

                switch (option) {
                    case 1 -> {
                        recipe.append("+SHOT");
                        view.print("  ✓ Added Extra Shot");
                    }
                    case 2 -> {
                        recipe.append("+OAT");
                        view.print("  ✓ Added Oat Milk");
                    }
                    case 3 -> {
                        recipe.append("+SYP");
                        view.print("  ✓ Added Syrup");
                    }
                    case 4 -> {
                        recipe.append("+L");
                        view.print("  ✓ Made Large");
                    }
                    case 0 -> customizing = false;
                    default -> view.print("  ❌ Invalid option");
                }
            }
        }

        // Command Pattern: use PosRemote to track command for undo
        Command addCmd = new AddItemCommand(orderService, recipe.toString(), 1);
        remote.setSlot(SLOT_ADD_ITEM, addCmd);
        remote.press(SLOT_ADD_ITEM);
        orderRepo.save(order);
    }

    private static void addFromRecipe(Order order, OrderService orderService) {
        view.print("\n--- Recipe Builder (Command Pattern) ---");
        view.print("Base codes: ESP, LAT, CAP, AME, CCK, CRO, MUF");
        view.print("Add-ons: SHOT, OAT, SYP, L");
        view.print("Example: LAT+SHOT+OAT+L");
        System.out.print("Enter recipe: ");

        String recipe = scanner.nextLine().trim().toUpperCase();
        System.out.print("Quantity: ");
        int qty = getIntInput();

        try {
            // Command Pattern: create and execute command
            Command addCmd = new AddItemCommand(orderService, recipe, qty);
            remote.setSlot(SLOT_ADD_ITEM, addCmd);
            remote.press(SLOT_ADD_ITEM);
            orderRepo.save(order);
        } catch (Exception e) {
            view.print("❌ Invalid recipe: " + e.getMessage());
        }
    }

    private static boolean handlePayment(Order order, OrderService orderService, OrderFSM fsm) {
        showOrderSummary(order);

        // Strategy Pattern: PricingService with DiscountPolicy/TaxPolicy
        var pricing = pricingService.price(order.subtotal());
        view.print("\n--- Pricing (Strategy Pattern) ---");
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
        view.print("║ 4. Back to Order (add more items)  ║");
        view.print("║ 5. Cancel Order                    ║");
        view.print("╚════════════════════════════════════╝");
        System.out.print("Choose payment method (Strategy Pattern): ");

        int choice = getIntInput();

        try {
            switch (choice) {
                case 1 -> {
                    return processCashPayment(order, orderService, fsm);
                }
                case 2 -> {
                    System.out.print("Enter card number: ");
                    String cardNum = scanner.nextLine().trim();

                    if (cardNum.length() != 16 || !cardNum.matches("\\d+")) {
                        view.print("❌ Invalid card number format!");
                        return false;
                    }

                    view.print("\n💳 Processing card payment...");
                    processPayment(order, orderService, fsm, new CardPayment("****" + cardNum));
                    return false;
                }
                case 3 -> {
                    System.out.print("Enter wallet ID: ");
                    String walletId = scanner.nextLine().trim();

                    if (walletId.isEmpty()) {
                        view.print("❌ Wallet ID cannot be empty!");
                        return false;
                    }

                    view.print("\n📱 Processing wallet payment...");
                    processPayment(order, orderService, fsm, new WalletPayment(walletId));
                    return false;
                }
                case 4 -> {
                    return false;
                }
                case 5 -> {
                    System.out.print("⚠️  Are you sure you want to cancel? (y/n): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("y")) {
                        fsm.cancel();
                        view.print("❌ Order cancelled");
                        return true;
                    }
                    return false;
                }
                default -> {
                    view.print("❌ Invalid option!");
                    return false;
                }
            }
        } catch (Exception e) {
            view.print("❌ Payment failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean processCashPayment(Order order, OrderService orderService, OrderFSM fsm) {
        Money total = pricingService.price(order.subtotal()).total();

        view.print("\n💵 Cash Payment");
        view.print("   Total due: " + total + " EUR");

        Money cashReceived = Money.zero();
        boolean validAmount = false;

        while (!validAmount) {
            System.out.print("   Cash received: ");
            try {
                double amount = Double.parseDouble(scanner.nextLine().trim());
                cashReceived = Money.of(amount);

                if (cashReceived.compareTo(total) < 0) {
                    Money shortfall = total.subtract(cashReceived);
                    view.print("   ❌ Insufficient! Still need: " + shortfall + " EUR");
                } else {
                    validAmount = true;
                }
            } catch (NumberFormatException e) {
                view.print("   ❌ Invalid amount. Please enter a number.");
            }
        }

        Money change = cashReceived.subtract(total);

        view.print("\n   Cash received: " + cashReceived + " EUR");
        if (change.compareTo(Money.zero()) > 0) {
            view.print("   💰 Change due: " + change + " EUR");
        } else {
            view.print("   ✓ Exact amount - No change");
        }

        processPayment(order, orderService, fsm, new CashPayment());
        return false;
    }

    private static void processPayment(Order order, OrderService orderService, OrderFSM fsm, PaymentStrategy strategy) {
        // Command Pattern: payment command
        Command payCmd = new PayOrderCommand(orderService, strategy, TAX_PERCENT);
        remote.setSlot(SLOT_PAY, payCmd);
        remote.press(SLOT_PAY);

        // State Pattern: transition
        fsm.pay();
        view.print("✓ Payment successful!");
        view.print("[FSM] State: " + fsm.status());

        // EventBus: emit event
        eventBus.emit(new OrderPaid(order.id()));
        orderRepo.save(order);
    }

    private static boolean handlePreparingState(Order order, OrderFSM fsm) {
        view.print("\n╔════════════ PREPARING ORDER ═══════════╗");
        view.print("║ Order #" + order.id() + " is being prepared       ║");
        view.print("║                                        ║");
        view.print("║ 1. Mark Order Ready (for pickup)       ║");
        view.print("║ 2. View Receipt                        ║");
        view.print("║ 3. Print Receipt (Adapter Pattern)     ║");
        view.print("╚════════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                view.print("\n📦 Marking order ready...");
                fsm.markReady();
                order.markReady();
                view.print("✓ Order ready for pickup/delivery!");
                view.print("[FSM] State: " + fsm.status());
            }
            case 2 -> showReceipt(order);
            case 3 -> {
                // Adapter Pattern: use legacy printer
                String receipt = checkoutService.checkout(order.id(), TAX_PERCENT);
                view.print("\n[Adapter] Sending to legacy thermal printer...");
                receiptPrinter.print(receipt);
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
        view.print("║ 3. Print Receipt (Adapter Pattern) ║");
        view.print("╚════════════════════════════════════╝");
        System.out.print("Choose option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1 -> {
                fsm.deliver();
                view.print("\n✓ Order #" + order.id() + " delivered!");
                view.print("Thank you for your business! ☕");
                view.print("[FSM] State: " + fsm.status());
                return true;
            }
            case 2 -> showReceipt(order);
            case 3 -> {
                // Adapter Pattern
                String receipt = checkoutService.checkout(order.id(), TAX_PERCENT);
                view.print("\n[Adapter] Sending to legacy thermal printer...");
                receiptPrinter.print(receipt);
            }
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
            int itemNum = 1;
            for (LineItem li : order.items()) {
                view.print(String.format("  %d. %-27s x%d", itemNum++, li.product().name(), li.quantity()));
                view.print(String.format("     %37s", li.lineTotal() + " EUR"));
            }
        }

        view.print("  ─────────────────────────────────────");
        view.print("  Subtotal: " + order.subtotal() + " EUR");
        view.print("  Tax (10%): " + order.taxAtPercent(10) + " EUR");
        view.print("  ─────────────────────────────────────");
        view.print("  TOTAL: " + order.totalWithTax(10) + " EUR");
        view.print("╚════════════════════════════════════════╝");
    }

    private static void showReceipt(Order order) {
        view.print("\n╔════════════════════════════════════════╗");
        view.print("║            CAFÉ RECEIPT                ║");
        view.print("╠════════════════════════════════════════╣");
        view.print("  Order #" + order.id());
        view.print("  Date: " + java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        view.print("  ────────────────────────────────────");

        // MVC: Controller delegates to CheckoutService
        view.print("[MVC] Controller -> CheckoutService -> View");
        view.print(controller.checkout(order.id(), TAX_PERCENT));

        view.print("╠════════════════════════════════════════╣");
        view.print("║   Thank you for your order! ☕         ║");
        view.print("║   Please come again!                   ║");
        view.print("╚════════════════════════════════════════╝");

        view.print("\nPress Enter to continue...");
        scanner.nextLine();
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

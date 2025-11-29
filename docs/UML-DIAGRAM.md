# Café POS System - Complete UML Class Diagram

## Quick Reference

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CAFÉ POS SYSTEM - ARCHITECTURE                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐    │
│  │                         PRESENTATION LAYER (ui)                              │    │
│  │  ┌──────────────────┐    ┌─────────────────┐                                │    │
│  │  │  OrderController │───▶│   ConsoleView   │                                │    │
│  │  └────────┬─────────┘    └─────────────────┘                                │    │
│  └───────────┼──────────────────────────────────────────────────────────────────┘    │
│              │                                                                       │
│  ┌───────────┼──────────────────────────────────────────────────────────────────┐    │
│  │           ▼              APPLICATION LAYER (app)                              │    │
│  │  ┌──────────────────┐    ┌─────────────────┐    ┌────────────────────┐       │    │
│  │  │ CheckoutService  │───▶│ ReceiptFormatter│    │     EventBus       │       │    │
│  │  └────────┬─────────┘    └─────────────────┘    │  ┌──────────────┐  │       │    │
│  │           │                                      │  │ OrderCreated │  │       │    │
│  │           │                                      │  │  OrderPaid   │  │       │    │
│  │           │                                      │  └──────────────┘  │       │    │
│  │           │                                      └────────────────────┘       │    │
│  └───────────┼───────────────────────────────────────────────────────────────────┘    │
│              │                                                                       │
│  ┌───────────┼──────────────────────────────────────────────────────────────────┐    │
│  │           ▼                   DOMAIN LAYER                                    │    │
│  │  ┌──────────────────┐    ┌─────────────────┐    ┌──────────────────┐         │    │
│  │  │ OrderRepository  │◀───│      Order      │───▶│    LineItem      │         │    │
│  │  │   «interface»    │    │  «aggregate»    │    │                  │         │    │
│  │  └──────────────────┘    │                 │    └────────┬─────────┘         │    │
│  │                          │ OrderPublisher  │             │                   │    │
│  │  ┌──────────────────┐    │ OrderObserver   │             ▼                   │    │
│  │  │    OrderIds      │    └─────────────────┘    ┌──────────────────┐         │    │
│  │  └──────────────────┘                           │     Product      │         │    │
│  │                                                 │   «interface»    │         │    │
│  └─────────────────────────────────────────────────┴──────────────────┴─────────┘    │
│                                                                                       │
│  ┌──────────────────────────────────────────────────────────────────────────────┐    │
│  │                        INFRASTRUCTURE LAYER (infra)                           │    │
│  │  ┌────────────────────────┐    ┌─────────────────┐    ┌──────────────────┐   │    │
│  │  │ InMemoryOrderRepository│    │     Wiring      │───▶│   Components     │   │    │
│  │  └────────────────────────┘    │   «DI Container»│    │    «record»      │   │    │
│  │                                └─────────────────┘    └──────────────────┘   │    │
│  └──────────────────────────────────────────────────────────────────────────────┘    │
│                                                                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns

### 1. Decorator Pattern (decorator/)

```
                    ┌───────────────┐
                    │   «interface» │
                    │    Product    │
                    └───────┬───────┘
                            │
            ┌───────────────┼───────────────┐
            │               │               │
            ▼               ▼               ▼
    ┌───────────────┐ ┌───────────────┐ ┌─────────────────────┐
    │ SimpleProduct │ │   «interface» │ │ «abstract»          │
    │   «final»     │ │    Priced     │ │  ProductDecorator   │
    └───────────────┘ └───────────────┘ │                     │
                                        │ # base: Product     │
                                        └──────────┬──────────┘
                                                   │
                    ┌──────────────┬───────────────┼───────────────┬──────────────┐
                    │              │               │               │              │
                    ▼              ▼               ▼               ▼              │
            ┌───────────────┐ ┌───────────┐ ┌───────────┐ ┌───────────────┐      │
            │   ExtraShot   │ │  OatMilk  │ │   Syrup   │ │   SizeLarge   │      │
            │ +$0.80        │ │ +$0.50    │ │ +$0.40    │ │ +$0.70        │      │
            └───────────────┘ └───────────┘ └───────────┘ └───────────────┘      │
```

### 2. Strategy Pattern (payment/, checkout/)

```
Payment Strategies:                          Pricing Policies:

┌─────────────────────┐                     ┌──────────────────┐    ┌──────────────┐
│    «interface»      │                     │   «interface»    │    │ «interface»  │
│  PaymentStrategy    │                     │  DiscountPolicy  │    │  TaxPolicy   │
│  + pay(Order): void │                     │ +discountOf(Money)│    │+taxOn(Money) │
└──────────┬──────────┘                     └────────┬─────────┘    └──────┬───────┘
           │                                         │                     │
     ┌─────┼─────┐                          ┌────────┼────────┐           │
     │     │     │                          │        │        │           │
     ▼     ▼     ▼                          ▼        ▼        ▼           ▼
┌────────┐┌────────┐┌────────┐      ┌────────┐┌──────────┐┌────────┐┌──────────────┐
│  Cash  ││  Card  ││ Wallet │      │Loyalty ││ NoDis-   ││ Fixed  ││FixedRateTax  │
│Payment ││Payment ││Payment │      │Percent ││ count    ││ Coupon ││   Policy     │
└────────┘└────────┘└────────┘      └────────┘└──────────┘└────────┘└──────────────┘
```

### 3. Observer Pattern (domain/, observer/)

```
┌─────────────────────────┐           ┌──────────────────────┐
│     «interface»         │           │     «interface»      │
│    OrderPublisher       │◀─────────▶│    OrderObserver     │
│ + register(Observer)    │           │ + updated(Order,     │
│ + unregister(Observer)  │           │           String)    │
│ + notifyObservers()     │           └──────────┬───────────┘
└───────────┬─────────────┘                      │
            │                          ┌─────────┼─────────┐
            │                          │         │         │
            ▼                          ▼         ▼         ▼
    ┌───────────────┐          ┌─────────────┐┌────────────┐┌────────────────┐
    │     Order     │          │KitchenDisplay││DeliveryDesk││CustomerNotifier│
    │ «aggregate»   │          └─────────────┘└────────────┘└────────────────┘
    └───────────────┘
```

### 4. Command Pattern (command/)

```
                        ┌───────────────────┐
                        │    «interface»    │
                        │      Command      │
                        │  + execute()      │
                        │  + undo()         │
                        └─────────┬─────────┘
                                  │
            ┌─────────────────────┼─────────────────────┐
            │                     │                     │
            ▼                     ▼                     ▼
    ┌───────────────┐     ┌───────────────┐     ┌───────────────┐
    │AddItemCommand │     │PayOrderCommand│     │ MacroCommand  │
    │               │     │               │     │ steps: Command│
    └───────┬───────┘     └───────┬───────┘     └───────────────┘
            │                     │
            └──────────┬──────────┘
                       ▼
               ┌───────────────┐         ┌───────────────┐
               │ OrderService  │◀────────│   PosRemote   │
               │               │         │  «invoker»    │
               └───────────────┘         │  slots: []    │
                                         │  history: []  │
                                         └───────────────┘
```

### 5. State Pattern (state/)

```
                              ┌────────────────┐
                              │    OrderFSM    │
                              │ - state: State │
                              │ + pay()        │
                              │ + prepare()    │
                              │ + markReady()  │
                              │ + deliver()    │
                              │ + cancel()     │
                              └───────┬────────┘
                                      │ delegates to
                                      ▼
                              ┌───────────────┐
                              │ «interface»   │
                              │    State      │
                              └───────┬───────┘
                                      │
        ┌────────────┬────────────┬───┴───┬────────────┬────────────┐
        │            │            │       │            │            │
        ▼            ▼            ▼       ▼            ▼            ▼
   ┌─────────┐ ┌───────────┐ ┌─────────┐ ┌──────────┐ ┌──────────┐
   │NewState │─▶│Preparing  │─▶│Ready   │─▶│Delivered│ │Cancelled │
   │         │ │   State   │ │  State  │ │  State   │ │  State   │
   └─────────┘ └───────────┘ └─────────┘ └──────────┘ └──────────┘
       │                │                                   ▲
       └────────────────┴───────────────────────────────────┘
                        (cancel transitions)
```

### 6. Composite Pattern (menu/)

```
                         ┌──────────────────────┐
                         │ «abstract»           │
                         │   MenuComponent      │
                         │ + add(Component)     │
                         │ + remove(Component)  │
                         │ + getChild(int)      │
                         │ + name(): String     │
                         │ + price(): BigDecimal│
                         │ + print(): void      │
                         │ + iterator()         │
                         └──────────┬───────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
           ┌────────────────┐              ┌────────────────┐
           │     Menu       │              │    MenuItem    │
           │  «composite»   │              │    «leaf»      │
           │                │              │                │
           │ - name: String │              │ - name: String │
           │ - children: [] │              │ - price        │
           │                │              │ - vegetarian   │
           └───────┬────────┘              └────────────────┘
                   │
                   │ 0..*
                   ▼
           ┌────────────────┐
           │ MenuComponent  │
           └────────────────┘
```

### 7. Adapter Pattern (printing/)

```
    ┌───────────────┐          ┌─────────────────────┐          ┌─────────────────────┐
    │  «interface»  │          │ LegacyPrinterAdapter│          │ LegacyThermalPrinter│
    │    Printer    │◀─────────│     «adapter»       │─────────▶│     «adaptee»       │
    │               │          │                     │          │                     │
    │ +print(String)│          │ -adaptee            │          │+legacyPrint(byte[]) │
    └───────────────┘          │ +print(String)      │          └─────────────────────┘
                               └─────────────────────┘
```

### 8. Factory Pattern (factory/)

```
    ┌─────────────────────────────────────────────────────────────┐
    │                     ProductFactory                           │
    │  + create(String recipe): Product                           │
    │                                                              │
    │  Recipe DSL: "BASE+ADDON1+ADDON2+..."                       │
    │                                                              │
    │  Base codes:  ESP=Espresso, LAT=Latte, CAP=Cappuccino       │
    │  Addon codes: SHOT=ExtraShot, OAT=OatMilk, SYP=Syrup, L=Large│
    └───────────────────────────────┬─────────────────────────────┘
                                    │ creates
                                    ▼
                            ┌───────────────┐
                            │    Product    │
                            │ (decorated)   │
                            └───────────────┘
```

---

## Complete Class Listing by Package

### common/
| Class | Type | Description |
|-------|------|-------------|
| `Money` | final class | Value object for monetary amounts |

### domain/
| Class | Type | Description |
|-------|------|-------------|
| `Order` | final class | Aggregate root, implements OrderPublisher |
| `LineItem` | final class | Order line with product and quantity |
| `OrderRepository` | interface | Port for persistence |
| `OrderObserver` | interface | Observer callback |
| `OrderPublisher` | interface | Observable interface |
| `OrderIds` | final class | ID generator |

### catalog/
| Class | Type | Description |
|-------|------|-------------|
| `Product` | interface | Product contract |
| `SimpleProduct` | final class | Basic product implementation |
| `Catalog` | interface | Product catalog port |
| `InMemoryCatalog` | final class | In-memory catalog |

### decorator/
| Class | Type | Description |
|-------|------|-------------|
| `Priced` | interface | Price getter contract |
| `ProductDecorator` | abstract class | Base decorator |
| `ExtraShot` | final class | +$0.80 decorator |
| `OatMilk` | final class | +$0.50 decorator |
| `Syrup` | final class | +$0.40 decorator |
| `SizeLarge` | final class | +$0.70 decorator |

### factory/
| Class | Type | Description |
|-------|------|-------------|
| `ProductFactory` | final class | Creates products from recipe DSL |

### payment/
| Class | Type | Description |
|-------|------|-------------|
| `PaymentStrategy` | interface | Payment method contract |
| `CashPayment` | final class | Cash payment |
| `CardPayment` | final class | Card payment |
| `WalletPayment` | final class | Digital wallet payment |

### observer/
| Class | Type | Description |
|-------|------|-------------|
| `KitchenDisplay` | final class | Kitchen observer |
| `DeliveryDesk` | final class | Delivery observer |
| `CustomerNotifier` | final class | Customer notification observer |

### command/
| Class | Type | Description |
|-------|------|-------------|
| `Command` | interface | Command contract |
| `AddItemCommand` | final class | Add item command |
| `PayOrderCommand` | final class | Pay order command |
| `MacroCommand` | final class | Composite command |
| `PosRemote` | final class | Command invoker |
| `OrderService` | final class | Command receiver |

### state/
| Class | Type | Description |
|-------|------|-------------|
| `State` | interface | State contract |
| `OrderFSM` | class | State machine context |
| `NewState` | class | Initial state |
| `PreparingState` | class | Preparation state |
| `ReadyState` | class | Ready for pickup state |
| `DeliveredState` | class | Final delivered state |
| `CancelledState` | class | Cancelled state |

### menu/
| Class | Type | Description |
|-------|------|-------------|
| `MenuComponent` | abstract class | Composite component |
| `Menu` | class | Composite node |
| `MenuItem` | class | Leaf node |
| `CompositeIterator` | class | Depth-first iterator |

### printing/
| Class | Type | Description |
|-------|------|-------------|
| `Printer` | interface | Target interface |
| `LegacyPrinterAdapter` | final class | Adapter |
| `LegacyThermalPrinter` | class | Adaptee (vendor.legacy) |

### checkout/
| Class | Type | Description |
|-------|------|-------------|
| `DiscountPolicy` | interface | Discount strategy |
| `LoyaltyPercentDiscount` | final class | Percentage discount |
| `NoDiscount` | final class | No discount |
| `FixedCouponDiscount` | final class | Fixed amount discount |
| `TaxPolicy` | interface | Tax strategy |
| `FixedRateTaxPolicy` | final class | Fixed rate tax |
| `PricingService` | final class | Pricing orchestrator |
| `PricingResult` | record | Pricing result DTO |
| `ReceiptPrinter` | final class | Receipt formatter |

### app/
| Class | Type | Description |
|-------|------|-------------|
| `CheckoutService` | final class | Checkout use case |
| `ReceiptFormatter` | final class | Receipt formatting |

### app/events/
| Class | Type | Description |
|-------|------|-------------|
| `EventBus` | final class | Pub-sub event bus |
| `OrderEvent` | sealed interface | Event marker |
| `OrderCreated` | record | Order created event |
| `OrderPaid` | record | Order paid event |

### infra/
| Class | Type | Description |
|-------|------|-------------|
| `InMemoryOrderRepository` | final class | Repository implementation |
| `Wiring` | final class | DI container |
| `Components` | record | Wired components |

### ui/
| Class | Type | Description |
|-------|------|-------------|
| `OrderController` | final class | UI controller |
| `ConsoleView` | final class | Console view |

---

## Generating the Diagram

The PlantUML source is available at: `docs/class-diagram.puml`

To render it:

1. **Online:** Paste contents at [plantuml.com](https://www.plantuml.com/plantuml)
2. **VS Code:** Install "PlantUML" extension
3. **IntelliJ:** Install "PlantUML integration" plugin
4. **Command line:**
   ```bash
   java -jar plantuml.jar docs/class-diagram.puml
   ```

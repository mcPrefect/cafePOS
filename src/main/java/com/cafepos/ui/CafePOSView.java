// package com.cafepos.ui;

// import com.cafepos.catalog.*;
// import com.cafepos.common.Money;
// import com.cafepos.decorator.*;
// import com.cafepos.domain.*;
// import com.cafepos.factory.ProductFactory;
// import com.vaadin.flow.component.button.Button;
// import com.vaadin.flow.component.button.ButtonVariant;
// import com.vaadin.flow.component.combobox.ComboBox;
// import com.vaadin.flow.component.html.*;
// import com.vaadin.flow.component.notification.Notification;
// import com.vaadin.flow.component.notification.NotificationVariant;
// import com.vaadin.flow.component.orderedlayout.*;
// import com.vaadin.flow.component.textfield.TextField;
// import com.vaadin.flow.router.Route;

// import java.util.ArrayList;
// import java.util.List;

// @Route("")
// public class CafePOSView extends VerticalLayout {
    
//     private final Order order;
//     private final Catalog catalog;
//     private final ProductFactory factory;
//     private final VerticalLayout orderItemsLayout;
//     private final H3 totalLabel;
    
//     public CafePOSView() {
//         // Initialize
//         this.order = new Order(OrderIds.next());
//         this.catalog = createCatalog();
//         this.factory = new ProductFactory();
        
//         setSizeFull();
//         setPadding(true);
//         setSpacing(true);
//         getStyle()
//             .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
//             .set("min-height", "100vh");
        
//         // Main container
//         VerticalLayout mainContainer = new VerticalLayout();
//         mainContainer.setWidth("900px");
//         mainContainer.getStyle()
//             .set("background", "white")
//             .set("border-radius", "16px")
//             .set("box-shadow", "0 20px 60px rgba(0,0,0,0.3)")
//             .set("padding", "30px");
//         setHorizontalComponentAlignment(Alignment.CENTER, mainContainer);
        
//         // Header
//         H1 title = new H1("☕ Café POS System");
//         title.getStyle()
//             .set("color", "#667eea")
//             .set("margin", "0")
//             .set("font-size", "2.5em");
        
//         H4 orderTitle = new H4("Order #" + order.id());
//         orderTitle.getStyle().set("color", "#666").set("margin-top", "0");
        
//         // Product Selection Section
//         Div selectionCard = createCard();
        
//         H3 selectionHeader = new H3("Add Items");
//         selectionHeader.getStyle().set("margin-top", "0");
        
//         ComboBox<Product> productSelect = new ComboBox<>("Select Product");
//         productSelect.setItems(getAllProducts());
//         productSelect.setItemLabelGenerator(p -> p.name() + " - " + p.basePrice() + " EUR");
//         productSelect.setWidth("100%");
        
//         TextField recipeField = new TextField("Factory Recipe");
//         recipeField.setPlaceholder("e.g., ESP+SHOT+OAT+L");
//         recipeField.setWidth("100%");
//         recipeField.setHelperText("Base: ESP, LAT, CAP | Add-ons: SHOT, OAT, SYP, L");
        
//         Button addButton = new Button("Add to Order", e -> {
//             if (productSelect.getValue() != null) {
//                 order.addItem(new LineItem(productSelect.getValue(), 1));
//                 updateOrderSummary();
//                 showSuccess("Added " + productSelect.getValue().name());
//                 productSelect.clear();
//             } else if (!recipeField.isEmpty()) {
//                 try {
//                     Product p = factory.create(recipeField.getValue());
//                     order.addItem(new LineItem(p, 1));
//                     updateOrderSummary();
//                     showSuccess("Added " + p.name());
//                     recipeField.clear();
//                 } catch (Exception ex) {
//                     showError("Invalid recipe: " + ex.getMessage());
//                 }
//             } else {
//                 showError("Please select a product or enter a recipe");
//             }
//         });
//         addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//         addButton.getStyle().set("width", "100%");
        
//         selectionCard.add(selectionHeader, productSelect, recipeField, addButton);
        
//         // Order Summary Section
//         Div orderCard = createCard();
        
//         H3 orderHeader = new H3("Current Order");
//         orderHeader.getStyle().set("margin-top", "0");
        
//         orderItemsLayout = new VerticalLayout();
//         orderItemsLayout.setPadding(false);
//         orderItemsLayout.setSpacing(true);
        
//         totalLabel = new H3("Total: 0.00 EUR");
//         totalLabel.getStyle()
//             .set("color", "#667eea")
//             .set("font-size", "1.8em")
//             .set("margin", "20px 0 10px 0");
        
//         Button clearButton = new Button("Clear Order", e -> {
//             // Create new order
//             Order newOrder = new Order(OrderIds.next());
//             // Copy items to new list to avoid modification during iteration
//             List<LineItem> itemsToClear = new ArrayList<>(order.items());
//             for (LineItem item : itemsToClear) {
//                 order.items().remove(item);
//             }
//             updateOrderSummary();
//             showSuccess("Order cleared");
//         });
//         clearButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        
//         Button checkoutButton = new Button("Checkout", e -> {
//             if (order.items().isEmpty()) {
//                 showError("Order is empty!");
//             } else {
//                 Notification notification = Notification.show(
//                     "💳 Total: " + order.totalWithTax(10) + " EUR\n" +
//                     "Subtotal: " + order.subtotal() + " EUR\n" +
//                     "Tax (10%): " + order.taxAtPercent(10) + " EUR",
//                     5000,
//                     Notification.Position.MIDDLE
//                 );
//                 notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//             }
//         });
//         checkoutButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
//         checkoutButton.getStyle().set("width", "100%").set("padding", "15px");
        
//         HorizontalLayout buttonLayout = new HorizontalLayout(clearButton, checkoutButton);
//         buttonLayout.setWidthFull();
//         checkoutButton.getStyle().set("flex", "1");
        
//         orderCard.add(orderHeader, orderItemsLayout, totalLabel, buttonLayout);
        
//         // Add everything to main container
//         mainContainer.add(title, orderTitle, selectionCard, orderCard);
        
//         add(mainContainer);
//         updateOrderSummary();
//     }
    
//     private Div createCard() {
//         Div card = new Div();
//         card.getStyle()
//             .set("background", "#f8f9fa")
//             .set("border-radius", "12px")
//             .set("padding", "20px")
//             .set("margin-bottom", "20px");
//         return card;
//     }
    
//     private Catalog createCatalog() {
//         Catalog cat = new InMemoryCatalog();
//         cat.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
//         cat.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.20)));
//         cat.add(new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00)));
//         cat.add(new SimpleProduct("P-AME", "Americano", Money.of(2.80)));
//         cat.add(new SimpleProduct("P-CCK", "Chocolate Cookie", Money.of(3.50)));
//         cat.add(new SimpleProduct("P-CRO", "Croissant", Money.of(2.75)));
//         return cat;
//     }
    
//     private List<Product> getAllProducts() {
//         List<Product> products = new ArrayList<>();
//         products.add(catalog.findById("P-ESP").orElseThrow());
//         products.add(catalog.findById("P-LAT").orElseThrow());
//         products.add(catalog.findById("P-CAP").orElseThrow());
//         products.add(catalog.findById("P-AME").orElseThrow());
//         products.add(catalog.findById("P-CCK").orElseThrow());
//         products.add(catalog.findById("P-CRO").orElseThrow());
//         return products;
//     }
    
//     private void updateOrderSummary() {
//         orderItemsLayout.removeAll();
        
//         if (order.items().isEmpty()) {
//             Paragraph emptyMessage = new Paragraph("No items yet");
//             emptyMessage.getStyle().set("color", "#999").set("font-style", "italic");
//             orderItemsLayout.add(emptyMessage);
//         } else {
//             for (LineItem li : order.items()) {
//                 Div itemDiv = new Div();
//                 itemDiv.getStyle()
//                     .set("background", "white")
//                     .set("padding", "12px")
//                     .set("border-radius", "8px")
//                     .set("margin-bottom", "8px");
                
//                 HorizontalLayout itemLayout = new HorizontalLayout();
//                 itemLayout.setWidthFull();
//                 itemLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
//                 itemLayout.setAlignItems(Alignment.CENTER);
                
//                 Span itemName = new Span(li.product().name() + " ×" + li.quantity());
//                 itemName.getStyle().set("font-weight", "500");
                
//                 Span itemPrice = new Span(li.lineTotal() + " EUR");
//                 itemPrice.getStyle()
//                     .set("color", "#667eea")
//                     .set("font-weight", "bold")
//                     .set("font-size", "1.1em");
                
//                 itemLayout.add(itemName, itemPrice);
//                 itemDiv.add(itemLayout);
//                 orderItemsLayout.add(itemDiv);
//             }
//         }
        
//         totalLabel.setText("Total: " + order.totalWithTax(10) + " EUR");
//     }
    
//     private void showSuccess(String message) {
//         Notification notification = Notification.show("✓ " + message, 3000, Notification.Position.TOP_CENTER);
//         notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
//     }
    
//     private void showError(String message) {
//         Notification notification = Notification.show("✗ " + message, 3000, Notification.Position.TOP_CENTER);
//         notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
//     }
// }
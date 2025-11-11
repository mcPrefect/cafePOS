package com.cafepos.demo;

import com.cafepos.menu.*;

import java.math.BigDecimal;

public class Week9Demo_Menu {
    public static void main(String[] args) {
        // Build a nested menu structure
        Menu root = new Menu("Café Menu");
        Menu drinks = new Menu("Drinks");
        Menu coffee = new Menu("Coffee");
        Menu desserts = new Menu("Desserts");

        coffee.add(new MenuItem("Espresso", new BigDecimal("2.50"), true));
        coffee.add(new MenuItem("Latte", new BigDecimal("3.20"), true));
        coffee.add(new MenuItem("Cappuccino", new BigDecimal("3.20"), true));

        drinks.add(coffee);
        drinks.add(new MenuItem("Orange Juice", new BigDecimal("2.40"), true));
        drinks.add(new MenuItem("Hot Chocolate", new BigDecimal("2.80"), true));

        desserts.add(new MenuItem("Brownie", new BigDecimal("2.00"), true));
        desserts.add(new MenuItem("Cheesecake", new BigDecimal("2.80"), true));

        root.add(drinks);
        root.add(desserts);

        System.out.println("=== FULL MENU ===");
        root.print();

        System.out.println("\n=== VEGETARIAN ONLY ===");
        root.vegetarianItems().forEach(MenuItem::print);

        System.out.println("\n=== DEPTH-FIRST ORDER (names) ===");
        for (MenuComponent mc : root) {
            System.out.println(mc.name());
        }
    }
}
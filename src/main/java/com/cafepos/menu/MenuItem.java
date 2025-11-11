package com.cafepos.menu;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Leaf in the Composite. */
public class MenuItem extends MenuComponent {
    private final String name;
    private final BigDecimal price;
    private final boolean vegetarian;

    public MenuItem(String name, BigDecimal price, boolean vegetarian) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (price == null) throw new IllegalArgumentException("price required");
        this.name = name;
        this.price = price;
        this.vegetarian = vegetarian;
    }

    @Override public String name() { return name; }
    @Override public BigDecimal price() { return price; }
    @Override public boolean isVegetarian() { return vegetarian; }

    @Override
    public void print() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
        System.out.println("  - " + name + (vegetarian ? " (v)" : "") + " ... " + nf.format(price));
    }
}
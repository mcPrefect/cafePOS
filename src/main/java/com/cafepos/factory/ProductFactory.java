package com.cafepos.factory;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.SizeLarge;
import com.cafepos.decorator.Syrup;

/**
 * Factory for creating products from recipe strings.
 * 
 * Recipe format: BASE+ADDON1+ADDON2+...
 * 
 * Base codes:
 * ESP - Espresso (2.50)
 * LAT - Latte (3.20)
 * CAP - Cappuccino (3.00)
 * 
 * Addon codes:
 * SHOT - Extra Shot (+0.80)
 * OAT - Oat Milk (+0.50)
 * SYP - Syrup (+0.40)
 * L - Large Size (+0.70)
 * 
 * Examples:
 * "ESP+SHOT+OAT" = Espresso + Extra Shot + Oat Milk (3.80)
 * "LAT+L" = Latte (Large) (3.90)
 */
public final class ProductFactory {

    public Product create(String recipe) {
        if (recipe == null || recipe.isBlank()) {
            throw new IllegalArgumentException("recipe required");
        }

        // Split by '+' and normalize (trim and uppercase)
        String[] raw = recipe.split("\\+"); // literal '+'
        String[] parts = java.util.Arrays.stream(raw)
                .map(String::trim)
                .map(String::toUpperCase)
                .toArray(String[]::new);

        // First token is the base product
        Product p = switch (parts[0]) {
            case "ESP" -> new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
            case "LAT" -> new SimpleProduct("P-LAT", "Latte", Money.of(3.20));
            case "CAP" -> new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00));
            default -> throw new IllegalArgumentException("Unknown base: " + parts[0]);
        };

        // Apply each addon in order
        for (int i = 1; i < parts.length; i++) {
            p = switch (parts[i]) {
                case "SHOT" -> new ExtraShot(p);
                case "OAT" -> new OatMilk(p);
                case "SYP" -> new Syrup(p);
                case "L" -> new SizeLarge(p);
                default -> throw new IllegalArgumentException("Unknown addon: " + parts[i]);
            };
        }

        return p;
    }
}
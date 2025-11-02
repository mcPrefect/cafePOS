package com.cafepos.demo;

import com.cafepos.catalog.Product;
import com.cafepos.catalog.SimpleProduct;
import com.cafepos.common.Money;
import com.cafepos.decorator.ExtraShot;
import com.cafepos.decorator.OatMilk;
import com.cafepos.decorator.Priced;
import com.cafepos.decorator.SizeLarge;

public class DecoratorTest {
    public static void main(String[] args) {
        // Create base product
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        
        // Stack decorators
        Product decorated = new SizeLarge(
            new OatMilk(
                new ExtraShot(espresso)
            )
        );
        
        System.out.println("Name: " + decorated.name());
        System.out.println("Price: " + ((Priced) decorated).price());
        
        // Expected output:
        // Name: Espresso + Extra Shot + Oat Milk (Large)
        // Price: 4.50 EUR
    }
}

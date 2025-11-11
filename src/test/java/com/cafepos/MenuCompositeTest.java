package com.cafepos;

import com.cafepos.menu.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MenuCompositeTest {

    @Test
    void depthFirstTraversalOrder() {
        Menu root = new Menu("Root");
        Menu a = new Menu("A");
        Menu b = new Menu("B");
        root.add(a);
        root.add(b);
        a.add(new MenuItem("a1", new BigDecimal("1.00"), true));
        a.add(new MenuItem("a2", new BigDecimal("2.00"), false));
        Menu aSub = new Menu("A-Sub");
        aSub.add(new MenuItem("a3", new BigDecimal("3.00"), true));
        a.add(aSub);
        b.add(new MenuItem("b1", new BigDecimal("4.00"), true));

        List<String> seen = new ArrayList<>();
        for (MenuComponent mc : root) {
            seen.add(mc.name());
        }
        assertEquals(List.of("Root", "A", "a1", "a2", "A-Sub", "a3", "B", "b1"), seen,
                "Depth-first order mismatch");
    }

    @Test
    void vegetarianFilter() {
        Menu root = new Menu("Menu");
        root.add(new MenuItem("Beef Pie", new BigDecimal("3.50"), false));
        root.add(new MenuItem("Veggie Wrap", new BigDecimal("3.00"), true));
        root.add(new MenuItem("Brownie", new BigDecimal("2.00"), true));

        List<String> veg = root.vegetarianItems().map(MenuItem::name).collect(Collectors.toList());
        assertEquals(List.of("Veggie Wrap", "Brownie"), veg, "Vegetarian subset mismatch");
    }
}
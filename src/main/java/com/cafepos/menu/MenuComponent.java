package com.cafepos.menu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Base class for the Composite pattern.
 * "Safety over transparency": operations that don't apply to a role
 * throw UnsupportedOperationException by default.
 */
public abstract class MenuComponent implements Iterable<MenuComponent> {

    // ----- structural operations (only valid for Menu) -----
    public void add(MenuComponent component) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support add()");
    }

    public void remove(MenuComponent component) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support remove()");
    }

    public MenuComponent getChild(int i) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support getChild()");
    }

    /** Direct-children iterator for Menu; leaves throw by default. */
    public Iterator<MenuComponent> childrenIterator() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not have childrenIterator()");
    }

    // ----- data (only valid for MenuItem) -----
    public String name() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not have name()");
    }

    public BigDecimal price() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not have price()");
    }

    public boolean isVegetarian() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not have isVegetarian()");
    }

    /** Print this component (Menu prints subtree, MenuItem prints itself). */
    public void print() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " does not implement print()");
    }

    // ----- iteration -----
    /**
     * Default iterator for leaves: a single-element iterator that yields "this".
     * Menu overrides to return a depth-first iterator over the subtree.
     */
    @Override
    public Iterator<MenuComponent> iterator() {
        return new Iterator<MenuComponent>() {
            private boolean consumed = false;
            @Override public boolean hasNext() { return !consumed; }
            @Override public MenuComponent next() {
                if (consumed) throw new NoSuchElementException();
                consumed = true;
                return MenuComponent.this;
            }
        };
    }

    /** Stream of all MenuItems in this subtree (or the item itself if leaf). */
    public Stream<MenuItem> allItems() {
        var list = new ArrayList<MenuItem>();
        for (MenuComponent mc : this) {
            if (mc instanceof MenuItem) list.add((MenuItem) mc);
        }
        return list.stream();
    }

    /** Vegetarian subset convenience. */
    public Stream<MenuItem> vegetarianItems() {
        return allItems().filter(MenuItem::isVegetarian);
    }
}
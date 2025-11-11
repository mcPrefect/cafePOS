package com.cafepos.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Menu extends MenuComponent implements Iterable<MenuComponent> {
    private final String name;
    private final List<MenuComponent> children = new ArrayList<>();

    public Menu(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.name = name;
    }

    @Override public String name() { return name; }

    @Override
    public void add(MenuComponent component) {
        if (component == null) throw new IllegalArgumentException("component cannot be null");
        children.add(component);
    }

    @Override
    public void remove(MenuComponent component) {
        children.remove(component);
    }

    @Override
    public MenuComponent getChild(int i) { return children.get(i); }

    @Override
    public Iterator<MenuComponent> childrenIterator() { return children.iterator(); }

    @Override
    public Iterator<MenuComponent> iterator() {
        return new CompositeIterator(this);
    }

    @Override
    public void print() {
        System.out.println(name.toUpperCase());
        for (MenuComponent c : children) {
            c.print();
        }
    }
}

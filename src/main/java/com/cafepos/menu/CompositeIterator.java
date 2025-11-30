package com.cafepos.menu;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Depth-first iterator over a Menu tree. */
public class CompositeIterator implements Iterator<MenuComponent> {
    private final MenuComponent root;
    private boolean emittedRoot = false;
    private final Deque<Iterator<MenuComponent>> stack = new ArrayDeque<>();

    public CompositeIterator(MenuComponent root) {
        if (root == null) throw new IllegalArgumentException("root cannot be null");
        this.root = root;
    }

    @Override
    public boolean hasNext() {
        if (!emittedRoot) return true;
        while (!stack.isEmpty()) {
            if (stack.peek().hasNext()) return true;
            stack.pop();
        }
        return false;
    }

    @Override
    public MenuComponent next() {
        if (!emittedRoot) {
            emittedRoot = true;
            // push children AFTER emitting root (if root is a Menu)
            if (root instanceof Menu menu) {
                stack.push(menu.childrenIterator());
            }
            return root;
        }
        while (!stack.isEmpty()) {
            Iterator<MenuComponent> it = stack.peek();
            if (it.hasNext()) {
                MenuComponent next = it.next();
                if (next instanceof Menu menu) {
                    stack.push(menu.childrenIterator());
                }
                return next;
            } else {
                stack.pop();
            }
        }
        throw new NoSuchElementException();
    }
}
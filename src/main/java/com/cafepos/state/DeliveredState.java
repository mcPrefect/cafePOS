package com.cafepos.state;

public class DeliveredState implements State {
    @Override public String name() { return "DELIVERED"; }

    @Override
    public void pay(OrderFSM fsm) {
        System.out.println("Already delivered.");
    }

    @Override
    public void prepare(OrderFSM fsm) {
        System.out.println("Already delivered.");
    }

    @Override
    public void markReady(OrderFSM fsm) {
        System.out.println("Already delivered.");
    }

    @Override
    public void deliver(OrderFSM fsm) {
        System.out.println("Already delivered.");
    }

    @Override
    public void cancel(OrderFSM fsm) {
        System.out.println("Cannot cancel: already delivered.");
    }
}
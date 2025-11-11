package com.cafepos.state;

public class CancelledState implements State {
    @Override public String name() { return "CANCELLED"; }

    @Override
    public void pay(OrderFSM fsm) {
        System.out.println("Cannot pay: order is cancelled.");
    }

    @Override
    public void prepare(OrderFSM fsm) {
        System.out.println("Cannot prepare: order is cancelled.");
    }

    @Override
    public void markReady(OrderFSM fsm) {
        System.out.println("Cannot mark ready: order is cancelled.");
    }

    @Override
    public void deliver(OrderFSM fsm) {
        System.out.println("Cannot deliver: order is cancelled.");
    }

    @Override
    public void cancel(OrderFSM fsm) {
        System.out.println("Already cancelled.");
    }
}

package com.cafepos.state;

public class PreparingState implements State {
    @Override public String name() { return "PREPARING"; }

    @Override
    public void pay(OrderFSM fsm) {
        System.out.println("Already paid.");
    }

    @Override
    public void prepare(OrderFSM fsm) {
        System.out.println("Already preparing...");
        // stays in PREPARING
    }

    @Override
    public void markReady(OrderFSM fsm) {
        System.out.println("Order ready for pickup.");
        fsm.setState(new ReadyState());
    }

    @Override
    public void deliver(OrderFSM fsm) {
        System.out.println("Cannot deliver: not ready.");
    }

    @Override
    public void cancel(OrderFSM fsm) {
        System.out.println("Order cancelled.");
        fsm.setState(new CancelledState());
    }
}
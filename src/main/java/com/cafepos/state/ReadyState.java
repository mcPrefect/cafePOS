package com.cafepos.state;

public class ReadyState implements State {
    @Override public String name() { return "READY"; }

    @Override
    public void pay(OrderFSM fsm) {
        System.out.println("Already paid.");
    }

    @Override
    public void prepare(OrderFSM fsm) {
        System.out.println("Already prepared.");
    }

    @Override
    public void markReady(OrderFSM fsm) {
        System.out.println("Already ready.");
    }

    @Override
    public void deliver(OrderFSM fsm) {
        System.out.println("Delivered to customer.");
        fsm.setState(new DeliveredState());
    }

    @Override
    public void cancel(OrderFSM fsm) {
        System.out.println("Cannot cancel: already ready for delivery.");
    }
}
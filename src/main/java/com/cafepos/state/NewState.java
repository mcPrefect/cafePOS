package com.cafepos.state;

public class NewState implements State {
    @Override public String name() { return "NEW"; }

    @Override
    public void pay(OrderFSM fsm) {
        System.out.println("Payment accepted; starting preparation.");
        fsm.setState(new PreparingState());
    }

    @Override
    public void prepare(OrderFSM fsm) {
        System.out.println("Cannot prepare: order not paid yet.");
    }

    @Override
    public void markReady(OrderFSM fsm) {
        System.out.println("Cannot mark ready: not prepared.");
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
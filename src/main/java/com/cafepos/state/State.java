package com.cafepos.state;

public interface State {
    String name();
    void pay(OrderFSM fsm);
    void prepare(OrderFSM fsm);
    void markReady(OrderFSM fsm);
    void deliver(OrderFSM fsm);
    void cancel(OrderFSM fsm);
}
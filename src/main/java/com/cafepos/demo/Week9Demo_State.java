package com.cafepos.demo;

import com.cafepos.state.*;

public class Week9Demo_State {
    public static void main(String[] args) {
        OrderFSM fsm = new OrderFSM();
        System.out.println("Initial: " + fsm.status());

        fsm.prepare();   // invalid
        fsm.pay();       // NEW -> PREPARING
        System.out.println("After pay: " + fsm.status());

        fsm.prepare();   // idempotent
        fsm.markReady(); // -> READY
        System.out.println("After markReady: " + fsm.status());

        fsm.cancel();    // invalid in READY
        fsm.deliver();   // -> DELIVERED
        System.out.println("After deliver: " + fsm.status());

        // Any further actions are invalid
        fsm.pay();
    }
}
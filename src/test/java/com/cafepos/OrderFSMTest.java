package com.cafepos;

import com.cafepos.state.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderFSMTest {

    @Test
    void happyPath() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        fsm.pay();
        assertEquals("PREPARING", fsm.status());

        fsm.markReady();
        assertEquals("READY", fsm.status());

        fsm.deliver();
        assertEquals("DELIVERED", fsm.status());
    }

    @Test
    void illegalTransitionsDoNotChangeState() {
        OrderFSM fsm = new OrderFSM();
        fsm.prepare(); // illegal
        assertEquals("NEW", fsm.status());

        fsm.cancel(); // -> CANCELLED
        assertEquals("CANCELLED", fsm.status());

        fsm.pay();
        fsm.prepare();
        fsm.markReady();
        fsm.deliver();
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status());
    }
}
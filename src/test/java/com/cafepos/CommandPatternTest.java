package com.cafepos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.cafepos.command.AddItemCommand;
import com.cafepos.command.Command;
import com.cafepos.command.OrderService;
import com.cafepos.command.PosRemote;
import com.cafepos.order.Order;
import com.cafepos.order.OrderIds;

class CommandPatternTest {
    
    @Test
    void command_executes_and_adds_item() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        
        Command addCmd = new AddItemCommand(service, "ESP", 1);
        addCmd.execute();
        
        assertEquals(1, order.items().size());
    }
    
    @Test
    void undo_removes_last_item() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        
        Command addCmd = new AddItemCommand(service, "ESP", 1);
        addCmd.execute();
        addCmd.undo();
        
        assertEquals(0, order.items().size());
    }
    
    @Test
    void remote_executes_command_in_slot() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(2);
        
        remote.setSlot(0, new AddItemCommand(service, "LAT+L", 2));
        remote.press(0);
        
        assertEquals(1, order.items().size());
    }
}
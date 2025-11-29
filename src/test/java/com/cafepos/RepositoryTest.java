package com.cafepos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.cafepos.domain.Order;
import com.cafepos.infra.InMemoryOrderRepository;

class RepositoryTest {

    @Test
    void saveAndFind() {
        var repo = new InMemoryOrderRepository();
        var order = new Order(123);

        repo.save(order);
        var found = repo.findById(123);

        assertTrue(found.isPresent());
        assertEquals(123, found.get().id());
    }

    @Test
    void findNonExistentReturnsEmpty() {
        var repo = new InMemoryOrderRepository();

        var found = repo.findById(999);

        assertTrue(found.isEmpty());
    }

    @Test
    void saveOverwritesExisting() {
        var repo = new InMemoryOrderRepository();
        var order1 = new Order(1);
        var order2 = new Order(1);

        repo.save(order1);
        repo.save(order2);

        assertSame(order2, repo.findById(1).get());
    }
}

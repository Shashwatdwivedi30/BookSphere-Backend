package com.booksphere.orderservice;

import com.booksphere.orderservice.exception.OrderException;
import com.booksphere.orderservice.exception.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionTest {

    @Test
    void testExceptions() {
        OrderException e1 = new OrderException("Error1");
        assertEquals("Error1", e1.getMessage());

        OrderNotFoundException e2 = new OrderNotFoundException("Error2");
        assertEquals("Error2", e2.getMessage());
    }
}

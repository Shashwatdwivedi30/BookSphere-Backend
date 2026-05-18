package com.booksphere.cartservice;

import com.booksphere.cartservice.exception.CartException;
import com.booksphere.cartservice.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionTest {

    @Test
    void testExceptions() {
        CartException e1 = new CartException("Error1");
        assertEquals("Error1", e1.getMessage());

        InsufficientStockException e2 = new InsufficientStockException("Error2");
        assertEquals("Error2", e2.getMessage());
    }
}

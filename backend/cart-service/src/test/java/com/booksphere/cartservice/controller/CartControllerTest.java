package com.booksphere.cartservice.controller;

import com.booksphere.cartservice.model.Cart;
import com.booksphere.cartservice.model.CartItem;
import com.booksphere.cartservice.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @Test
    void testGetCart() {
        when(cartService.getCart(anyString())).thenReturn(new Cart());
        assertNotNull(cartController.getCart("u1"));
    }

    @Test
    void testAddItem() {
        when(cartService.addToCart(anyString(), any(CartItem.class))).thenReturn(new Cart());
        assertNotNull(cartController.addToCart("u1", new CartItem()));
    }

    @Test
    void testRemoveItem() {
        when(cartService.removeFromCart(anyString(), anyString())).thenReturn(new Cart());
        assertNotNull(cartController.removeFromCart("u1", "b1"));
    }

    @Test
    void testClearCart() {
        when(cartService.clearCart(anyString())).thenReturn(new Cart());
        assertNotNull(cartController.clearCart("u1"));
    }
}

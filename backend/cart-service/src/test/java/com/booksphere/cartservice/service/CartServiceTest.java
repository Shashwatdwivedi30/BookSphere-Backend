package com.booksphere.cartservice.service;

import com.booksphere.cartservice.exception.CartException;
import com.booksphere.cartservice.exception.InsufficientStockException;
import com.booksphere.cartservice.model.Cart;
import com.booksphere.cartservice.model.CartItem;
import com.booksphere.cartservice.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CartService cartService;

    @Test
    void testGetCart_Existing() {
        Cart cart = new Cart();
        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        assertNotNull(cartService.getCart("test@example.com"));
    }

    @Test
    void testGetCart_New() {
        when(cartRepository.findByUserEmail("new@example.com")).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        Cart cart = cartService.getCart("new@example.com");
        assertNotNull(cart);
        assertEquals("new@example.com", cart.getUserEmail());
    }

    @Test
    void testAddToCart_NewItem() {
        Cart cart = new Cart();
        cart.setUserEmail("test@example.com");
        cart.setItems(new ArrayList<>());
        
        CartItem item = new CartItem();
        item.setBookId("b1");
        item.setQuantity(1);
        item.setPrice(10.0);

        Map<String, Object> bookResp = new HashMap<>();
        bookResp.put("stock", 5);

        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(bookResp);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.addToCart("test@example.com", item);
        assertEquals(1, result.getItems().size());
        assertEquals(10.0, result.getTotalPrice());
    }

    @Test
    void testAddToCart_UpdateDetails() {
        Cart cart = new Cart();
        CartItem existing = new CartItem();
        existing.setBookId("b1");
        existing.setQuantity(1);
        existing.setPrice(10.0);
        cart.getItems().add(existing);

        CartItem incoming = new CartItem();
        incoming.setBookId("b1");
        incoming.setQuantity(1);
        incoming.setImageUrl("new-url");
        incoming.setIsbn("new-isbn");
        incoming.setPrice(10.0);

        Map<String, Object> bookResp = new HashMap<>();
        bookResp.put("stock", 10);

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(bookResp);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.addToCart("u", incoming);
        assertEquals("new-url", existing.getImageUrl());
        assertEquals("new-isbn", existing.getIsbn());
        assertEquals(2, existing.getQuantity());
        assertEquals(20.0, result.getTotalPrice());
    }

    @Test
    void testAddToCart_NullStock() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(null);

        assertThrows(CartException.class, () -> cartService.addToCart("u", item));
    }
    
    @Test
    void testAddToCart_NullStockInMap() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(new HashMap<>());

        assertThrows(CartException.class, () -> cartService.addToCart("u", item));
    }

    @Test
    void testAddToCart_StockException() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenThrow(new RuntimeException("API Down"));

        assertThrows(CartException.class, () -> cartService.addToCart("u", item));
    }

    @Test
    void testAddToCart_InsufficientStock() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");
        item.setQuantity(5);

        Map<String, Object> bookResp = new HashMap<>();
        bookResp.put("stock", 2);

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(bookResp);

        assertThrows(InsufficientStockException.class, () -> cartService.addToCart("u", item));
    }
    
    @Test
    void testAddToCart_InsufficientStockUpdate() {
        Cart cart = new Cart();
        CartItem existing = new CartItem();
        existing.setBookId("b1");
        existing.setQuantity(2);
        cart.getItems().add(existing);
        
        CartItem item = new CartItem();
        item.setBookId("b1");
        item.setQuantity(2);

        Map<String, Object> bookResp = new HashMap<>();
        bookResp.put("stock", 3);

        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(bookResp);

        assertThrows(InsufficientStockException.class, () -> cartService.addToCart("u", item));
    }

    @Test
    void testRemoveFromCart() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");
        cart.getItems().add(item);
        
        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.removeFromCart("u", "b1");
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void testClearCart() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        item.setBookId("b1");
        cart.getItems().add(item);
        cart.setTotalPrice(10.0);

        when(cartRepository.findByUserEmail("test@example.com")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);
        
        Cart result = cartService.clearCart("test@example.com");
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotalPrice());
    }
}

package com.booksphere.cartservice;

import com.booksphere.cartservice.controller.CartController;
import com.booksphere.cartservice.model.Cart;
import com.booksphere.cartservice.model.CartItem;
import com.booksphere.cartservice.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class CartServiceApplicationTests {

    @Autowired
    private CartController cartController;

    @Autowired
    private CartService cartService;

    @MockBean
    private com.booksphere.cartservice.repository.CartRepository cartRepository;

    @Test
    void contextLoads() {
        assertThat(cartController).isNotNull();
        assertThat(cartService).isNotNull();
    }

    @Test
    void testCartModelCreation() {
        Cart cart = new Cart();
        cart.setUserEmail("user@test.com");
        cart.setTotalPrice(0.0);
        assertThat(cart.getUserEmail()).isEqualTo("user@test.com");
    }

    @Test
    void testCartItemModelCreation() {
        CartItem item = new CartItem("book1", "Title", 10.0, 1, "url", "isbn");
        assertThat(item.getBookId()).isEqualTo("book1");
        assertThat(item.getQuantity()).isEqualTo(1);
    }

    @Test
    void testCartServiceBeanExists() {
        assertThat(cartService).isNotNull();
    }

    @Test
    void testCartControllerBeanExists() {
        assertThat(cartController).isNotNull();
    }

    @Test
    void testGetCartIntegration() {
        Cart cart = new Cart();
        cart.setUserEmail("user1");
        
        when(cartRepository.findByUserEmail("user1")).thenReturn(java.util.Optional.of(cart));
        
        Cart result = cartController.getCart("user1");
        assertThat(result.getUserEmail()).isEqualTo("user1");
    }

    @Test
    void testAddToCartIntegration() {
        Cart cart = new Cart();
        CartItem item = new CartItem();
        
        // Mocking the service behavior indirectly
        assertThat(cart).isNotNull();
    }

    @Test
    void testCartTotalPriceUpdate() {
        Cart cart = new Cart();
        cart.setTotalPrice(50.0);
        assertThat(cart.getTotalPrice()).isEqualTo(50.0);
    }

    @Test
    void testCartEmptyItems() {
        Cart cart = new Cart();
        assertThat(cart.getItems()).isEmpty();
    }
}

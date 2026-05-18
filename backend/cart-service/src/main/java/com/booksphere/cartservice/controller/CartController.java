package com.booksphere.cartservice.controller;

import com.booksphere.cartservice.model.Cart;
import com.booksphere.cartservice.model.CartItem;
import com.booksphere.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userEmail:.+}")
    public Cart getCart(@PathVariable String userEmail) {
        return cartService.getCart(userEmail);
    }

    @PostMapping("/add/{userEmail:.+}")
    public Cart addToCart(@PathVariable String userEmail, @RequestBody CartItem item) {
        return cartService.addToCart(userEmail, item);
    }

    @DeleteMapping("/remove/{userEmail:.+}/{bookId}")
    public Cart removeFromCart(@PathVariable String userEmail, @PathVariable String bookId) {
        return cartService.removeFromCart(userEmail, bookId);
    }

    @DeleteMapping("/clear/{userEmail:.+}")
    public Cart clearCart(@PathVariable String userEmail) {
        return cartService.clearCart(userEmail);
    }
}
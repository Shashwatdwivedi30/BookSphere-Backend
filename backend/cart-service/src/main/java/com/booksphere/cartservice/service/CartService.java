package com.booksphere.cartservice.service;

import com.booksphere.cartservice.exception.CartException;
import com.booksphere.cartservice.exception.InsufficientStockException;
import com.booksphere.cartservice.model.Cart;
import com.booksphere.cartservice.model.CartItem;
import com.booksphere.cartservice.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Get or create cart
    public Cart getCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserEmail(userEmail);
                    return cartRepository.save(cart);
                });
    }

    // Add item to cart
    public Cart addToCart(String userEmail, CartItem item) {
        Cart cart = getCart(userEmail);
        int incomingQuantity = item.getQuantity() == null ? 0 : item.getQuantity();
        Integer stock = fetchBookStock(item.getBookId());
        
        if (stock == null) {
            throw new CartException("Unable to verify stock for selected book");
        }

        updateOrAddItem(cart, item, incomingQuantity, stock);

        cart.getItems().removeIf(existingItem -> 
            existingItem.getQuantity() == null || existingItem.getQuantity() <= 0);

        updateTotal(cart);
        return cartRepository.save(cart);
    }

    private void updateOrAddItem(Cart cart, CartItem item, int incomingQuantity, int stock) {
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(i -> i.getBookId().equals(item.getBookId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int updatedQuantity = (existingItem.getQuantity() == null ? 0 : existingItem.getQuantity()) + incomingQuantity;
            validateStock(updatedQuantity, stock);
            
            existingItem.setQuantity(updatedQuantity);
            updateItemDetails(existingItem, item);
        } else if (incomingQuantity > 0) {
            validateStock(incomingQuantity, stock);
            cart.getItems().add(item);
        }
    }

    private void validateStock(int quantity, int stock) {
        if (quantity > stock) {
            throw new InsufficientStockException("Only " + stock + " item(s) available in stock");
        }
    }

    private void updateItemDetails(CartItem target, CartItem source) {
        if (source.getImageUrl() != null && !source.getImageUrl().isBlank()) {
            target.setImageUrl(source.getImageUrl());
        }
        if (source.getIsbn() != null && !source.getIsbn().isBlank()) {
            target.setIsbn(source.getIsbn());
        }
    }

    private Integer fetchBookStock(String bookId) {
        try {
            Map<String, Object> book = restTemplate.getForObject("http://book-service/books/" + bookId, Map.class);
            if (book == null || book.get("stock") == null) {
                return null;
            }
            return ((Number) book.get("stock")).intValue();
        } catch (Exception ex) {
            return null;
        }
    }

    // Remove item
    public Cart removeFromCart(String userEmail, String bookId) {
        Cart cart = getCart(userEmail);
        cart.getItems().removeIf(item -> item.getBookId().equals(bookId));
        updateTotal(cart);
        return cartRepository.save(cart);
    }

    // Clear cart
    public Cart clearCart(String userEmail) {
        Cart cart = getCart(userEmail);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        return cartRepository.save(cart);
    }

    // Calculate total
    private void updateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * (item.getQuantity() == null ? 0 : item.getQuantity()))
                .sum();
        cart.setTotalPrice(total);
    }
}
package com.booksphere.wishlistservice.service;

import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.model.WishlistItem;
import com.booksphere.wishlistservice.repository.WishlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Override
    public Wishlist addItem(String userId, WishlistItem item) {
        log.info("Adding book {} to wishlist for user {}", item.getBookId(), userId);
        Wishlist wishlist = getOrCreateWishlist(userId);
        
        // Check if item already exists
        boolean exists = wishlist.getItems().stream()
                .anyMatch(existingItem -> existingItem.getBookId().equals(item.getBookId()));
                
        if (!exists) {
            item.setAddedAt(LocalDateTime.now());
            wishlist.getItems().add(item);
        }
        
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist removeItem(String userId, String bookId) {
        log.info("Removing book {} from wishlist for user {}", bookId, userId);
        Wishlist wishlist = getOrCreateWishlist(userId);
        wishlist.getItems().removeIf(item -> item.getBookId().equals(bookId));
        return wishlistRepository.save(wishlist);
    }

    @Override
    public Wishlist getWishlist(String userId) {
        return getOrCreateWishlist(userId);
    }

    private Wishlist getOrCreateWishlist(String userId) {
        return wishlistRepository.findById(userId).orElseGet(() -> {
            log.info("Creating new wishlist for user {}", userId);
            Wishlist newWishlist = new Wishlist();
            newWishlist.setUserId(userId);
            return wishlistRepository.save(newWishlist);
        });
    }
}

package com.booksphere.wishlistservice.controller;

import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.model.WishlistItem;
import com.booksphere.wishlistservice.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/add/{userId:.+}")
    public Wishlist addItem(@PathVariable String userId, @RequestBody WishlistItem item) {
        return wishlistService.addItem(userId, item);
    }

    @DeleteMapping("/remove/{userId:.+}/{bookId}")
    public Wishlist removeItem(@PathVariable String userId, @PathVariable String bookId) {
        return wishlistService.removeItem(userId, bookId);
    }

    @GetMapping("/{userId:.+}")
    public Wishlist getWishlist(@PathVariable String userId) {
        return wishlistService.getWishlist(userId);
    }
}

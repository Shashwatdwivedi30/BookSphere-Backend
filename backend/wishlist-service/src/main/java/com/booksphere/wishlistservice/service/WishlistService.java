package com.booksphere.wishlistservice.service;

import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.model.WishlistItem;

public interface WishlistService {
    Wishlist addItem(String userId, WishlistItem item);
    Wishlist removeItem(String userId, String bookId);
    Wishlist getWishlist(String userId);
}

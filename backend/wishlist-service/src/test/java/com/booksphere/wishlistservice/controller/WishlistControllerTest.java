package com.booksphere.wishlistservice.controller;

import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.model.WishlistItem;
import com.booksphere.wishlistservice.service.WishlistService;
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
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    @Test
    void testGetWishlist() {
        when(wishlistService.getWishlist(anyString())).thenReturn(new Wishlist());
        assertNotNull(wishlistController.getWishlist("u1"));
    }

    @Test
    void testAddToWishlist() {
        when(wishlistService.addItem(anyString(), any(WishlistItem.class))).thenReturn(new Wishlist());
        assertNotNull(wishlistController.addItem("u1", new WishlistItem()));
    }

    @Test
    void testRemoveFromWishlist() {
        when(wishlistService.removeItem(anyString(), anyString())).thenReturn(new Wishlist());
        assertNotNull(wishlistController.removeItem("u1", "b1"));
    }
}

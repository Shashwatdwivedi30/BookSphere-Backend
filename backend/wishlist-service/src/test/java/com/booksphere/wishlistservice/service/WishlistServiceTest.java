package com.booksphere.wishlistservice.service;

import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.model.WishlistItem;
import com.booksphere.wishlistservice.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Test
    void testGetWishlist_Existing() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("u1");
        when(wishlistRepository.findById("u1")).thenReturn(Optional.of(wishlist));
        Wishlist result = wishlistService.getWishlist("u1");
        assertEquals("u1", result.getUserId());
    }

    @Test
    void testGetWishlist_New() {
        when(wishlistRepository.findById("u1")).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));
        Wishlist result = wishlistService.getWishlist("u1");
        assertEquals("u1", result.getUserId());
    }

    @Test
    void testAddItem_Existing() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("u1");
        WishlistItem item = new WishlistItem();
        item.setBookId("b1");
        wishlist.getItems().add(item);
        
        when(wishlistRepository.findById("u1")).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));
        
        Wishlist result = wishlistService.addItem("u1", item);
        assertEquals(1, result.getItems().size());
        verify(wishlistRepository, times(1)).save(any());
    }

    @Test
    void testAddItem_New() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("u1");
        when(wishlistRepository.findById("u1")).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));

        WishlistItem item = new WishlistItem();
        item.setBookId("b1");
        Wishlist result = wishlistService.addItem("u1", item);
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testRemoveItem() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("u1");
        WishlistItem item = new WishlistItem();
        item.setBookId("b1");
        wishlist.getItems().add(item);
        
        when(wishlistRepository.findById("u1")).thenReturn(Optional.of(wishlist));
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(i -> i.getArgument(0));

        Wishlist result = wishlistService.removeItem("u1", "b1");
        assertEquals(0, result.getItems().size());
    }
}

package com.booksphere.wishlistservice;

import com.booksphere.wishlistservice.controller.WishlistController;
import com.booksphere.wishlistservice.model.Wishlist;
import com.booksphere.wishlistservice.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class WishlistServiceApplicationTests {

    @Autowired
    private WishlistController wishlistController;

    @Autowired
    private WishlistService wishlistService;

    @MockBean
    private com.booksphere.wishlistservice.repository.WishlistRepository wishlistRepository;

    @Test
    void contextLoads() {
        assertThat(wishlistController).isNotNull();
        assertThat(wishlistService).isNotNull();
    }

    @Test
    void testWishlistModelCreation() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("user1");
        assertThat(wishlist.getUserId()).isEqualTo("user1");
    }

    @Test
    void testWishlistServiceBeanExists() {
        assertThat(wishlistService).isNotNull();
    }

    @Test
    void testWishlistControllerBeanExists() {
        assertThat(wishlistController).isNotNull();
    }

    @Test
    void testGetWishlistIntegration() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("user1");
        
        when(wishlistRepository.findById("user1")).thenReturn(java.util.Optional.of(wishlist));
        
        Wishlist result = wishlistController.getWishlist("user1");
        assertThat(result.getUserId()).isEqualTo("user1");
    }

    @Test
    void testWishlistItemsListNotNull() {
        Wishlist wishlist = new Wishlist();
        assertThat(wishlist.getItems()).isNotNull();
    }

    @Test
    void testWishlistAddItemSim() {
        Wishlist wishlist = new Wishlist();
        wishlist.getItems().add(new com.booksphere.wishlistservice.model.WishlistItem());
        assertThat(wishlist.getItems()).isNotEmpty();
    }

    @Test
    void testWishlistRemoveItemSim() {
        Wishlist wishlist = new Wishlist();
        com.booksphere.wishlistservice.model.WishlistItem item = new com.booksphere.wishlistservice.model.WishlistItem();
        wishlist.getItems().add(item);
        wishlist.getItems().remove(item);
        assertThat(wishlist.getItems()).isEmpty();
    }

    @Test
    void testWishlistUserEmailField() {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId("reader@booksphere.com");
        assertThat(wishlist.getUserId()).isEqualTo("reader@booksphere.com");
    }
}

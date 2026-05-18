package com.booksphere.wishlistservice.repository;

import com.booksphere.wishlistservice.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, String> {
}

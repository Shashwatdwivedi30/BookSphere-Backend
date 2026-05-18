package com.booksphere.reviewservice.repository;

import com.booksphere.reviewservice.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByBookId(String bookId);
    Optional<Review> findByBookIdAndUserId(String bookId, String userId);
}

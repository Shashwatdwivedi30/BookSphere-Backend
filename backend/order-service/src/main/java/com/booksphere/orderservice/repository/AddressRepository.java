package com.booksphere.orderservice.repository;

import com.booksphere.orderservice.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends MongoRepository<Address, String> {
    Optional<Address> findByCustomerId(String customerId);
}

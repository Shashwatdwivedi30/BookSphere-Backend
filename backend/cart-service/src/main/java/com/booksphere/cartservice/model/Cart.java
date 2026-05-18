package com.booksphere.cartservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "carts")
public class Cart {

    @Id
    private String id;

    private String userEmail;

    private List<CartItem> items = new ArrayList<>();

    private Double totalPrice = 0.0;
}
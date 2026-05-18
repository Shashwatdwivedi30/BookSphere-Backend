package com.booksphere.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "order_addresses")
public class Address {
    @Id
    private String id;
    private String customerId;
    private String fullName;
    private String mobileNumber;
    private String fullAddress;
    private String city;
    private String state;
    private String pincode;
}

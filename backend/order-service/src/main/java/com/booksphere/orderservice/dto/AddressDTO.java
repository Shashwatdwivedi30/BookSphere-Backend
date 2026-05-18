package com.booksphere.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String id;
    private String orderCustomerId;
    private String receiverName;
    private String receiverMobile;
    private String completeAddress;
    private String cityName;
    private String stateName;
    private String postalCode;
}

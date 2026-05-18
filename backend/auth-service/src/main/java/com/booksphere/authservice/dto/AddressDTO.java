package com.booksphere.authservice.dto;

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
    private String fullName;
    private String mobileNumber;
    private String fullAddress;
    private String city;
    private String state;
    private String pincode;
    private boolean isDefault;
}

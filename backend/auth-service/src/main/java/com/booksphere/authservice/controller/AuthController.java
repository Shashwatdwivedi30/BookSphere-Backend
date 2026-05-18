package com.booksphere.authservice.controller;

import com.booksphere.authservice.dto.LoginResponse;
import com.booksphere.authservice.dto.UserDTO;
import com.booksphere.authservice.dto.AddressDTO;
import com.booksphere.authservice.model.Address;
import com.booksphere.authservice.model.User;
import com.booksphere.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public org.springframework.http.ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
        String result = authService.register(convertToEntity(userDto));
        if (result.equals("User Registered Successfully")) {
            return org.springframework.http.ResponseEntity.ok(result);
        } else {
            return org.springframework.http.ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody UserDTO userDto) {
        return authService.login(convertToEntity(userDto));
    }

    @GetMapping("/profile")
    public String profile() {
        return "This is a protected profile API";
    }

    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return authService.getAllUsers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/profile/{email}")
    public UserDTO getUserProfile(@PathVariable String email) {
        return convertToDTO(authService.getUserByEmail(email));
    }

    @PutMapping("/profile/{email}")
    public UserDTO updateProfile(@PathVariable String email, @RequestBody UserDTO userDto) {
        return convertToDTO(authService.updateProfile(email, convertToEntity(userDto)));
    }

    @PostMapping("/forgot-password")
    public org.springframework.http.ResponseEntity<String> forgotPassword(@RequestBody com.booksphere.authservice.dto.ForgotPasswordRequest request) {
        String result = authService.sendOtp(request.getEmail());
        if (result.equals("OTP Sent Successfully")) {
            return org.springframework.http.ResponseEntity.ok(result);
        } else {
            return org.springframework.http.ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/reset-password")
    public org.springframework.http.ResponseEntity<String> resetPassword(@RequestBody com.booksphere.authservice.dto.ResetPasswordRequest request) {
        String result = authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        if (result.equals("Password Reset Successfully")) {
            return org.springframework.http.ResponseEntity.ok(result);
        } else {
            return org.springframework.http.ResponseEntity.badRequest().body(result);
        }
    }

    private UserDTO convertToDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .addresses(user.getAddresses() != null ? user.getAddresses().stream()
                        .map(this::convertAddressToDTO)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private User convertToEntity(UserDTO userDto) {
        if (userDto == null) return null;
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .role(userDto.getRole())
                .addresses(userDto.getAddresses() != null ? userDto.getAddresses().stream()
                        .map(this::convertAddressToEntity)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private AddressDTO convertAddressToDTO(Address address) {
        if (address == null) return null;
        return AddressDTO.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .mobileNumber(address.getMobileNumber())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .isDefault(address.isDefault())
                .build();
    }

    private Address convertAddressToEntity(AddressDTO addressDto) {
        if (addressDto == null) return null;
        return Address.builder()
                .id(addressDto.getId())
                .fullName(addressDto.getFullName())
                .mobileNumber(addressDto.getMobileNumber())
                .fullAddress(addressDto.getFullAddress())
                .city(addressDto.getCity())
                .state(addressDto.getState())
                .pincode(addressDto.getPincode())
                .isDefault(addressDto.isDefault())
                .build();
    }
}
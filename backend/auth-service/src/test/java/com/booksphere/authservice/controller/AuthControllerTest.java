package com.booksphere.authservice.controller;

import com.booksphere.authservice.dto.AddressDTO;
import com.booksphere.authservice.dto.LoginResponse;
import com.booksphere.authservice.dto.UserDTO;
import com.booksphere.authservice.model.Address;
import com.booksphere.authservice.model.User;
import com.booksphere.authservice.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegisterUser_Success() {
        AddressDTO addr = AddressDTO.builder().fullName("Name").build();
        UserDTO userDto = UserDTO.builder().email("test@example.com").password("pass").addresses(Collections.singletonList(addr)).build();
        when(authService.register(any(User.class))).thenReturn("User Registered Successfully");

        ResponseEntity<String> response = authController.registerUser(userDto);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testRegisterUser_Failure() {
        UserDTO userDto = UserDTO.builder().email("test@example.com").build();
        when(authService.register(any(User.class))).thenReturn("Email already exists");

        ResponseEntity<String> response = authController.registerUser(userDto);

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testLoginUser() {
        UserDTO userDto = UserDTO.builder().email("test@example.com").build();
        when(authService.login(any(User.class))).thenReturn(new LoginResponse());
        assertNotNull(authController.loginUser(userDto));
    }

    @Test
    void testProfile() {
        assertEquals("This is a protected profile API", authController.profile());
    }

    @Test
    void testGetAllUsers() {
        Address addr = Address.builder().fullName("Name").build();
        User user = User.builder().email("test@example.com").addresses(Collections.singletonList(addr)).build();
        when(authService.getAllUsers()).thenReturn(Collections.singletonList(user));
        assertNotNull(authController.getAllUsers());
    }

    @Test
    void testGetUserProfile() {
        User user = User.builder().email("test@example.com").build();
        when(authService.getUserByEmail(anyString())).thenReturn(user);
        assertNotNull(authController.getUserProfile("test@example.com"));
    }

    @Test
    void testUpdateProfile() {
        UserDTO userDto = UserDTO.builder().email("test@example.com").build();
        when(authService.updateProfile(anyString(), any(User.class))).thenReturn(new User());
        assertNotNull(authController.updateProfile("test@example.com", userDto));
    }

    @Test
    void testForgotPassword_Success() {
        com.booksphere.authservice.dto.ForgotPasswordRequest req = new com.booksphere.authservice.dto.ForgotPasswordRequest();
        req.setEmail("test@example.com");
        when(authService.sendOtp(anyString())).thenReturn("OTP Sent Successfully");
        assertEquals(200, authController.forgotPassword(req).getStatusCode().value());
    }

    @Test
    void testForgotPassword_Failure() {
        com.booksphere.authservice.dto.ForgotPasswordRequest req = new com.booksphere.authservice.dto.ForgotPasswordRequest();
        req.setEmail("test@example.com");
        when(authService.sendOtp(anyString())).thenReturn("Error");
        assertEquals(400, authController.forgotPassword(req).getStatusCode().value());
    }

    @Test
    void testResetPassword_Success() {
        com.booksphere.authservice.dto.ResetPasswordRequest req = new com.booksphere.authservice.dto.ResetPasswordRequest();
        req.setEmail("test@example.com");
        req.setOtp("123456");
        req.setNewPassword("pass");
        when(authService.resetPassword(anyString(), anyString(), anyString())).thenReturn("Password Reset Successfully");
        assertEquals(200, authController.resetPassword(req).getStatusCode().value());
    }

    @Test
    void testResetPassword_Failure() {
        com.booksphere.authservice.dto.ResetPasswordRequest req = new com.booksphere.authservice.dto.ResetPasswordRequest();
        req.setEmail("test@example.com");
        req.setOtp("123456");
        req.setNewPassword("pass");
        when(authService.resetPassword(anyString(), anyString(), anyString())).thenReturn("Error");
        assertEquals(400, authController.resetPassword(req).getStatusCode().value());
    }
}

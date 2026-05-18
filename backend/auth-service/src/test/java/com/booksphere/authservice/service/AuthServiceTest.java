package com.booksphere.authservice.service;

import com.booksphere.authservice.dto.LoginResponse;
import com.booksphere.authservice.model.Otp;
import com.booksphere.authservice.model.User;
import com.booksphere.authservice.repository.OtpRepository;
import com.booksphere.authservice.repository.UserRepository;
import com.booksphere.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setName("Test User");
        testUser.setRole("USER");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        String result = authService.register(testUser);
        assertEquals("User Registered Successfully", result);
    }

    @Test
    void testRegister_Exists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        String result = authService.register(testUser);
        assertEquals("Email already exists", result);
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("token");
        LoginResponse response = authService.login(testUser);
        assertNotNull(response.getToken());
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        LoginResponse response = authService.login(testUser);
        assertEquals("User not found", response.getError());
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        LoginResponse response = authService.login(testUser);
        assertEquals("Invalid Password", response.getError());
    }

    @Test
    void testSendOtp_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        String result = authService.sendOtp("test@example.com");
        assertEquals("OTP Sent Successfully", result);
        verify(rabbitTemplate).convertAndSend(nullable(String.class), nullable(String.class), any(com.booksphere.authservice.dto.NotificationEvent.class));
    }

    @Test
    void testSendOtp_RabbitFailure() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Rabbit Down")).when(rabbitTemplate).convertAndSend(nullable(String.class), nullable(String.class), any(com.booksphere.authservice.dto.NotificationEvent.class));
        String result = authService.sendOtp("test@example.com");
        assertEquals("Failed to send OTP", result);
    }

    @Test
    void testResetPassword_Success() {
        Otp otp = new Otp();
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        when(otpRepository.findByEmailAndOtpCode(anyString(), anyString())).thenReturn(Optional.of(otp));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncoded");
        
        String result = authService.resetPassword("test@example.com", "123456", "newPass");
        assertEquals("Password Reset Successfully", result);
    }

    @Test
    void testResetPassword_InvalidOtp() {
        when(otpRepository.findByEmailAndOtpCode(anyString(), anyString())).thenReturn(Optional.empty());
        String result = authService.resetPassword("test@example.com", "123456", "newPass");
        assertEquals("Invalid or Expired OTP", result);
    }

    @Test
    void testUpdateProfile_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        
        User update = new User();
        update.setName("New Name");
        update.setAddresses(Collections.singletonList(new com.booksphere.authservice.model.Address()));
        
        User result = authService.updateProfile("test@example.com", update);
        assertEquals("New Name", result.getName());
        assertNotNull(result.getAddresses());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(testUser));
        assertFalse(authService.getAllUsers().isEmpty());
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        assertNotNull(authService.getUserByEmail("test@example.com"));
    }
}

package com.booksphere.authservice.service;

import com.booksphere.authservice.dto.LoginResponse;
import com.booksphere.authservice.model.User;
import com.booksphere.authservice.repository.UserRepository;
import com.booksphere.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class AuthService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Role is always USER for public registration. Admin accounts are created directly in DB.
        user.setRole("USER");
        userRepository.save(user);

        return "User Registered Successfully";
    }

    public LoginResponse login(User user) {

        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser == null) {
            return new LoginResponse(null, null, null, null, null, USER_NOT_FOUND);
        }

        if (passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            String token = jwtUtil.generateToken(existingUser.getEmail(), existingUser.getRole());
            return new LoginResponse(token, existingUser.getId(), existingUser.getEmail(), existingUser.getName(), existingUser.getRole(), null);
        } else {
            return new LoginResponse(null, null, null, null, null, "Invalid Password");
        }
    }

    @Autowired
    private com.booksphere.authservice.repository.OtpRepository otpRepository;

    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @org.springframework.beans.factory.annotation.Value("${rabbitmq.exchange.name}")
    private String exchange;

    @org.springframework.beans.factory.annotation.Value("${rabbitmq.routing.key}")
    private String routingKey;

    public String sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return USER_NOT_FOUND;
        }

        // Generate 6-digit OTP
        String otpCode = String.valueOf(100000 + random.nextInt(900000));
        
        // Save OTP to DB
        otpRepository.deleteByEmail(email); // Clear old OTPs
        com.booksphere.authservice.model.Otp otp = new com.booksphere.authservice.model.Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);

        // Send OTP via RabbitMQ
        com.booksphere.authservice.dto.NotificationEvent event = com.booksphere.authservice.dto.NotificationEvent.builder()
                .userId(email)
                .userEmail(email)
                .userName(user.getName())
                .message("Your BookSphere OTP for password reset is: " + otpCode + ". It is valid for 5 minutes.")
                .type("PASSWORD_RESET")
                .build();
        
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            return "OTP Sent Successfully";
        } catch (Exception e) {
            return "Failed to send OTP";
        }
    }

    public String resetPassword(String email, String otp, String newPassword) {
        com.booksphere.authservice.model.Otp storedOtp = otpRepository.findByEmailAndOtpCode(email, otp).orElse(null);
        
        if (storedOtp == null || storedOtp.getExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            return "Invalid or Expired OTP";
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return USER_NOT_FOUND;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpRepository.deleteByEmail(email); // OTP used, delete it

        return "Password Reset Successfully";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));
    }

    public User updateProfile(String email, User updatedData) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        if (updatedData.getName() != null) {
            existingUser.setName(updatedData.getName());
        }
        
        if (updatedData.getAddresses() != null) {
            existingUser.setAddresses(updatedData.getAddresses());
        }

        return userRepository.save(existingUser);
    }
}
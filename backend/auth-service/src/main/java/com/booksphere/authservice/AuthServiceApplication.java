package com.booksphere.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.booksphere.authservice.repository.UserRepository;
import com.booksphere.authservice.model.User;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Ensure shashwatd3003@gmail.com is an ADMIN
            userRepository.findByEmail("shashwatd3003@gmail.com").ifPresent(user -> {
                if (!"ADMIN".equals(user.getRole())) {
                    user.setRole("ADMIN");
                    userRepository.save(user);
                    System.out.println("Elevated shashwatd3003@gmail.com to ADMIN role successfully!");
                }
            });

            // 2. Ensure Shashwat@gmail.com is an ADMIN as well (just in case they log in with this)
            userRepository.findByEmail("Shashwat@gmail.com").ifPresent(user -> {
                if (!"ADMIN".equals(user.getRole())) {
                    user.setRole("ADMIN");
                    userRepository.save(user);
                    System.out.println("Elevated Shashwat@gmail.com to ADMIN role successfully!");
                }
            });

            // 3. Ensure a default dedicated admin account exists
            if (!userRepository.existsByEmail("admin@booksphere.com")) {
                User admin = new User();
                admin.setEmail("admin@booksphere.com");
                admin.setName("BookSphere Admin");
                admin.setPassword(passwordEncoder.encode("AdminPassword123!"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Created default admin user: admin@booksphere.com / AdminPassword123!");
            }
        };
    }
}

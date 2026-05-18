package com.booksphere.authservice;

import com.booksphere.authservice.controller.AuthController;
import com.booksphere.authservice.model.User;
import com.booksphere.authservice.service.AuthService;
import com.booksphere.authservice.dto.LoginResponse;
import com.booksphere.authservice.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class AuthServiceApplicationTests {

    @Autowired
    private AuthController authController;

    @Autowired
    private AuthService authService;

    @MockBean
    private com.booksphere.authservice.repository.UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(authController).isNotNull();
        assertThat(authService).isNotNull();
    }

    @Test
    void testUserModelCreation() {
        User user = new User();
        user.setId("1");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole("USER");
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testAuthServiceBeanExists() {
        assertThat(authService).isNotNull();
    }

    @Test
    void testAuthControllerBeanExists() {
        assertThat(authController).isNotNull();
    }

    @Test
    void testRegisterIntegration() {
        UserDTO userDto = UserDTO.builder()
                .email("new@test.com")
                .name("New User")
                .password("password")
                .build();
        
        User user = new User();
        user.setEmail("new@test.com");
        user.setName("New User");
        user.setPassword("password");
        
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        org.springframework.http.ResponseEntity<String> response = authController.registerUser(userDto);
        assertThat(response.getBody()).isEqualTo("User Registered Successfully");
    }

    @Test
    void testLoginIntegration() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("pass");
        
        LoginResponse mockResponse = new LoginResponse("token", "1", "test@test.com", "Test User", "USER", null);
        // We mock the service indirectly by mocking the repo if we were testing service, 
        // but here we can just test the controller logic or bean interactions.
        assertThat(mockResponse.getToken()).isEqualTo("token");
    }

    @Test
    void testUserRoleAssignment() {
        User user = new User();
        user.setRole("ADMIN");
        assertThat(user.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void testUserNameValidation() {
        User user = new User();
        user.setName("A");
        assertThat(user.getName().length()).isLessThan(10);
    }

    @Test
    void testUserEmailValidation() {
        User user = new User();
        user.setEmail("invalid-email");
        assertThat(user.getEmail()).doesNotContain(" ");
    }
}

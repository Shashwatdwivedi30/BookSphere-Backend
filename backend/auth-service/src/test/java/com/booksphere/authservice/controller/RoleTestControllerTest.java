package com.booksphere.authservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RoleTestControllerTest {

    @InjectMocks
    private RoleTestController roleTestController;

    @Test
    void testUserAccess() {
        assertNotNull(roleTestController.userTest());
    }

    @Test
    void testAdminAccess() {
        assertNotNull(roleTestController.adminTest());
    }
}

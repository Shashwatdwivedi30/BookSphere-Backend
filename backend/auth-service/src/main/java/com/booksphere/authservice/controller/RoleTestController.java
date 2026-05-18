package com.booksphere.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/admin/test")
    public String adminTest() {
        return "Only ADMIN can access this";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "USER or ADMIN can access this";
    }
}
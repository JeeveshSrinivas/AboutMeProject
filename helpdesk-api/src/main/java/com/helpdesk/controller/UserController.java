
package com.helpdesk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.helpdesk.dto.request.RegisterRequest;
import com.helpdesk.dto.response.LoginResponse;
import com.helpdesk.dto.response.UserResponse;
import com.helpdesk.service.UserService;
import com.helpdesk.dto.request.LoginRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import com.helpdesk.dto.request.AdminCreateUserRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/admin/create-user")
    public ResponseEntity<UserResponse> createPrivilegedUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        UserResponse response = userService.createPrivilegedUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = userService.loginUser(request);

        return ResponseEntity.ok(response);
    }

}

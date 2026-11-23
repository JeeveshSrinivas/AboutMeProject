
package com.helpdesk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


import com.helpdesk.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import com.helpdesk.dto.request.AdminCreateUserRequest;
import com.helpdesk.dto.response.UserResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v2/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/admin/create-user")
    public ResponseEntity<UserResponse> createPrivilegedUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        UserResponse response = userService.createPrivilegedUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
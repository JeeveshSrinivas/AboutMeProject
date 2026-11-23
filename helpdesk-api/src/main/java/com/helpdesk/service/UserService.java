
package com.helpdesk.service;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helpdesk.dto.request.LoginRequest;
import com.helpdesk.dto.request.RegisterRequest;
import com.helpdesk.dto.response.LoginResponse;
import com.helpdesk.dto.response.UserResponse;
import com.helpdesk.entity.User;
import com.helpdesk.enums.UserRole;
import com.helpdesk.exception.InvalidCredentialsException;
import com.helpdesk.repository.jpa.UserRepository;
import com.helpdesk.dto.request.AdminCreateUserRequest;

import com.helpdesk.repository.RevokedTokenRepository;

import com.helpdesk.document.RevokedToken;
import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RevokedTokenRepository revokedTokenRepository;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RevokedTokenRepository revokedTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.REQUESTER);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    @Transactional
    public UserResponse createPrivilegedUser(AdminCreateUserRequest request) {

        // Verify that the logged-in user is an admin
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        User currentUser = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only admins can create privileged users");
        }

        // Allow only AGENT and ADMIN roles
        if (request.getRole() != UserRole.AGENT &&
                request.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException(
                    "Only AGENT or ADMIN accounts can be created here");
        }

        // Check whether email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // Create the user
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        // Prepare response
        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    public LoginResponse loginUser(LoginRequest request) {

        // Find user using email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Prepare user response
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setTokenType("Bearer");
        loginResponse.setUser(response);

        return loginResponse;
    }

    public void logoutUser(String token) {

        String tokenId = jwtService.extractTokenId(token);

        Instant expiresAt = jwtService.extractExpiration(token);

        RevokedToken revokedToken = new RevokedToken(
                tokenId,
                expiresAt);

        revokedTokenRepository.save(revokedToken);
    }

}

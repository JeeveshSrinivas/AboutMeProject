package com.helpdesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.helpdesk.dto.request.RegisterRequest;
import com.helpdesk.entity.User;
import com.helpdesk.enums.UserRole;
import com.helpdesk.repository.jpa.UserRepository;
import com.helpdesk.repository.RevokedTokenRepository;

import com.helpdesk.dto.request.LoginRequest;
import com.helpdesk.dto.response.LoginResponse;

import com.helpdesk.exception.InvalidCredentialsException;

import java.time.Instant;
import com.helpdesk.document.RevokedToken;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                passwordEncoder,
                jwtService,
                revokedTokenRepository);
    }

    @Test
    void shouldRegisterUserWithEncodedPassword() {

        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("Password@123");

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        // Act
        userService.registerUser(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(UserRole.REQUESTER, savedUser.getRole());

        assertNotEquals("Password@123", savedUser.getPassword());

        assertTrue(passwordEncoder.matches(
                "Password@123",
                savedUser.getPassword()));
    }

    @Test
    void shouldLoginUserWithValidCredentials() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setRole(UserRole.REQUESTER);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Password@123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(java.util.Optional.of(user));

        when(jwtService.generateToken(
                1L, "test@example.com", "REQUESTER"))
                .thenReturn("mock.signed.jwt");

        // Act
        LoginResponse response = userService.loginUser(request);

        // Assert
        assertNotNull(response.getToken());
        assertEquals("mock.signed.jwt", response.getToken());
        assertEquals("Bearer", response.getTokenType());

        assertNotNull(response.getUser());
        assertEquals("test@example.com", response.getUser().getEmail());

        verify(jwtService).generateToken(
                1L, "test@example.com", "REQUESTER");
    }

    @Test
    void shouldRejectLoginWithInvalidPassword() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("Password@123"));
        user.setRole(UserRole.REQUESTER);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword@123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(java.util.Optional.of(user));

        // Act and Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(request));

        verify(jwtService, never()).generateToken(
                anyLong(), anyString(), anyString());
    }

    @Test
    void shouldRejectLoginWithUnknownEmail() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Password@123");

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.loginUser(request));

        verify(jwtService, never()).generateToken(
                anyLong(), anyString(), anyString());
    }

    @Test
    void shouldRevokeTokenOnLogout() {

        // Arrange
        String token = "mock.signed.jwt";
        String tokenId = "unique-token-123";
        Instant expiration = Instant.now().plusSeconds(3600);

        when(jwtService.extractTokenId(token))
                .thenReturn(tokenId);

        when(jwtService.extractExpiration(token))
                .thenReturn(expiration);

        // Act
        userService.logoutUser(token);

        // Assert
        ArgumentCaptor<RevokedToken> captor = ArgumentCaptor.forClass(RevokedToken.class);

        verify(revokedTokenRepository).save(captor.capture());

        RevokedToken savedToken = captor.getValue();

        assertEquals(tokenId, savedToken.getTokenId());
        assertEquals(expiration, savedToken.getExpiresAt());
    }
}
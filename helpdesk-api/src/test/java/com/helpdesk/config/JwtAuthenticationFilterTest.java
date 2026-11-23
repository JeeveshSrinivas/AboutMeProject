package com.helpdesk.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockFilterChain;

import com.helpdesk.service.JwtService;
import com.helpdesk.repository.RevokedTokenRepository;

import io.jsonwebtoken.ExpiredJwtException;

import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtService,
                revokedTokenRepository
        );

        SecurityContextHolder.clearContext();
    }

    @Test
void shouldRejectRevokedToken() throws ServletException, IOException {

    // Arrange
    String token = "mock.signed.jwt";
    String tokenId = "unique-token-123";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.extractUserId(token)).thenReturn(1L);
    when(jwtService.extractTokenId(token)).thenReturn(tokenId);
    when(revokedTokenRepository.existsById(tokenId)).thenReturn(true);

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(401, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNull(filterChain.getRequest());

    verify(revokedTokenRepository).existsById(tokenId);
    verify(jwtService, never()).extractRole(token);
}

@Test
void shouldAuthenticateUserWithValidToken()
        throws ServletException, IOException {

    // Arrange
    String token = "mock.signed.jwt";
    String tokenId = "valid-token-123";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.extractUserId(token)).thenReturn(1L);
    when(jwtService.extractTokenId(token)).thenReturn(tokenId);
    when(revokedTokenRepository.existsById(tokenId)).thenReturn(false);
    when(jwtService.extractRole(token)).thenReturn("AGENT");

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(200, response.getStatus());
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());

    assertEquals(
            "1",
            SecurityContextHolder.getContext()
                    .getAuthentication().getName());

    assertTrue(
            SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getAuthorities()
                    .stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_AGENT")));

    assertNotNull(filterChain.getRequest());
}

@Test
void shouldRejectMalformedAuthorizationHeader()
        throws ServletException, IOException {

    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "InvalidToken abc123");

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(401, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNull(filterChain.getRequest());

    verifyNoInteractions(jwtService);
    verifyNoInteractions(revokedTokenRepository);
}

@Test
void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing()
        throws ServletException, IOException {

    // Arrange
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertNotNull(filterChain.getRequest());
    assertEquals(200, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());

    verifyNoInteractions(jwtService);
    verifyNoInteractions(revokedTokenRepository);
}

@Test
void shouldRejectTokenWithUnknownRole()
        throws ServletException, IOException {

    // Arrange
    String token = "mock.signed.jwt";
    String tokenId = "unknown-role-token";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.extractUserId(token)).thenReturn(1L);
    when(jwtService.extractTokenId(token)).thenReturn(tokenId);
    when(revokedTokenRepository.existsById(tokenId)).thenReturn(false);
    when(jwtService.extractRole(token)).thenReturn("SUPERUSER");

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(401, response.getStatus());
    assertNull(SecurityContextHolder.getContext().getAuthentication());
    assertNull(filterChain.getRequest());

    verify(jwtService).extractRole(token);
}

@Test
void shouldRejectTokenWithInvalidUserId()
        throws ServletException, IOException {

    // Arrange
    String token = "mock.signed.jwt";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.extractUserId(token)).thenReturn(0L);

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(401, response.getStatus());

    assertNull(SecurityContextHolder.getContext().getAuthentication());

    assertNull(filterChain.getRequest());

    verify(jwtService).extractUserId(token);
    verifyNoInteractions(revokedTokenRepository);
}

@Test
void shouldRejectExpiredToken()
        throws ServletException, IOException {

    // Arrange
    String token = "expired.mock.jwt";

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    when(jwtService.extractUserId(token))
            .thenThrow(new ExpiredJwtException(
                    null, null, "Token has expired"));

    // Act
    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    // Assert
    assertEquals(401, response.getStatus());

    assertNull(SecurityContextHolder.getContext().getAuthentication());

    assertNull(filterChain.getRequest());

    verify(jwtService).extractUserId(token);
    verifyNoInteractions(revokedTokenRepository);
}
}
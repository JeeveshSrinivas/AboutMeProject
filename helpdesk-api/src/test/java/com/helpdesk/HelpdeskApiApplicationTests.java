
package com.helpdesk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;
import com.helpdesk.entity.User;
import com.helpdesk.repository.jpa.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MvcResult;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class HelpdeskApiApplicationTests {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldReturn401WhenAccessingTicketsWithoutToken()
            throws Exception {

        mockMvc.perform(get("/api/v2/tickets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRegisterUserWithEncodedPasswordAndRequesterRole()
            throws Exception {

        // Act and Assert
        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Test User",
                            "email": "registration@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value("registration@test.com"))
                .andExpect(jsonPath("$.role").value("REQUESTER"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Verify database values
        User savedUser = userRepository.findByEmail("registration@test.com")
                .orElseThrow();

        assertTrue(passwordEncoder.matches(
                "Password@123", savedUser.getPassword()));

        assertNotEquals("Password@123", savedUser.getPassword());

        assertEquals(
                com.helpdesk.enums.UserRole.REQUESTER,
                savedUser.getRole());
    }

    @Test
    void shouldLoginUserWithValidCredentialsAndReturnJwtToken()
            throws Exception {

        // Arrange: Register a user first
        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Login Test User",
                            "email": "login@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isCreated());

        // Act and Assert: Login using the registered credentials
        mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "login@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("login@test.com"))
                .andExpect(jsonPath("$.user.role").value("REQUESTER"));
    }

    @Test
    void shouldAccessProtectedTicketEndpointWithValidJwt()
            throws Exception {

        // 1. Register a requester
        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Protected Test User",
                            "email": "protected@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isCreated());

        // 2. Login and capture the response
        MvcResult loginResult = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "protected@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn();

        // 3. Extract JWT from the login response
        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token");

        // 4. Access protected endpoint using JWT
        mockMvc.perform(get("/api/v2/tickets")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectJwtAfterLogout() throws Exception {

        // 1. Register a user
        mockMvc.perform(post("/api/v2/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fullName": "Logout Test User",
                            "email": "logout@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isCreated());

        // 2. Login and capture JWT
        MvcResult loginResult = mockMvc.perform(post("/api/v2/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "logout@test.com",
                            "password": "Password@123"
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token");

        // 3. Verify token works before logout
        mockMvc.perform(get("/api/v2/tickets")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // 4. Logout using the same JWT
        mockMvc.perform(post("/api/v2/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // 5. Verify revoked token is rejected
        mockMvc.perform(get("/api/v2/tickets")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}

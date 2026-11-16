
package com.helpdesk.validation;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class StartupConfigValidator {

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    @PostConstruct
    public void validateConfiguration() {

        if (dbPassword == null || dbPassword.isBlank()) {
            throw new IllegalStateException(
                    "Required configuration DB_PASSWORD is missing.");
        }

        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "Required configuration JWT_SECRET is missing.");
        }

        try {
            byte[] decodedSecret = Base64.getDecoder().decode(jwtSecret);

            if (decodedSecret.length < 32) {
                throw new IllegalStateException(
                        "JWT_SECRET must contain at least 256 bits.");
            }

        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "JWT_SECRET must be a valid Base64-encoded value.");
        }
    }
}


package com.helpdesk.validation;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;

@Component
public class StartupConfigValidator {

    @Value("${DB_PASSWORD:}")
    private String dbPassword;

    @Value("${app.jwt.secret:}")
    private String jwtSecret;

    private final Environment environment;

    public StartupConfigValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validateConfiguration() {

        boolean isTestProfile = environment.matchesProfiles("test");

        if (!isTestProfile &&
                (dbPassword == null || dbPassword.isBlank())) {

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

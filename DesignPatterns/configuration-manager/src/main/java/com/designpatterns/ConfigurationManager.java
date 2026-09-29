package com.designpatterns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    private final Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
        loadConfiguration();
    }

    private static class Holder {
        private static final ConfigurationManager INSTANCE =
                new ConfigurationManager();
    }

    public static ConfigurationManager getInstance() {
        return Holder.INSTANCE;
    }

    private void loadConfiguration() {

        try (InputStream input =
                     getClass().getClassLoader()
                             .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "config.properties file not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load configuration",
                    e
            );
        }
    }

    public String get(String key) {

        String value = properties.getProperty(key);

        if (value == null) {
            throw new IllegalArgumentException(
                    "Configuration key not found: " + key
            );
        }

        return value;
    }
}
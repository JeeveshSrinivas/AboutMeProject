package com.designpatterns;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {

    private static ConfigurationManager instance;

    private final Properties properties;

    private ConfigurationManager() {
        properties = new Properties();
        loadConfiguration();
    }

    public static ConfigurationManager getInstance() {

        if (instance == null) {
            instance = new ConfigurationManager();
        }

        return instance;
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
        return properties.getProperty(key);
    }
}
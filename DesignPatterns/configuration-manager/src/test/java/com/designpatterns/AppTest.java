package com.designpatterns;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void shouldReturnSameConfigurationManagerInstance() {

        ConfigurationManager manager1 =
                ConfigurationManager.getInstance();

        ConfigurationManager manager2 =
                ConfigurationManager.getInstance();

        assertSame(manager1, manager2);
    }

    @Test
    public void shouldReturnConfigurationValueForExistingKey() {

        ConfigurationManager manager =
                ConfigurationManager.getInstance();

        String value = manager.get("app.name");

        assertEquals("ConfigurationManagerDemo", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForMissingKey() {

        ConfigurationManager manager =
                ConfigurationManager.getInstance();

        manager.get("app.invalid");
    }
}
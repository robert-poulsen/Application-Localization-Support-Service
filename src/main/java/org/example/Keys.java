package org.example;

import java.io.IOException;
import java.util.Properties;

public class Keys {
    public static String loadProperty(final String key) {
        Properties properties = new Properties();
        try {
            properties.load(Keys.class.getClassLoader().getResourceAsStream("configuration.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty(key);
    }
}

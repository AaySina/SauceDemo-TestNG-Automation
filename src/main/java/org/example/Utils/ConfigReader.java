package org.example.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static Properties properties = new Properties();

    /**
     * @param env Environment name (staging, production, etc)
     * @return Properties object
     */
    public static Properties loadProperties(String env) {
        String fileName = "staging.properties";

        if (env != null && !env.isEmpty()) {
            fileName = env + ".properties";
        }

        String filePath = "src/test/resources/" + fileName;

        log.info("Attempting to load configuration from: {}", filePath); // Log sebelum mencoba load

        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
            log.info("Successfully loaded configuration from: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to load config file: {}. Ensure the file exists and the path is correct.", filePath, e);
            throw new RuntimeException("Cannot load config file: " + filePath + ". Cause: " + e.getMessage());
        }

        return properties;
    }

    /**
     * Load default properties (staging.properties)
     */
    public static void loadProperties() {
        loadProperties(null);
    }

    /**
     * Get property value by key
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
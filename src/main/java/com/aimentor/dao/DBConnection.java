package com.aimentor.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection pool manager using HikariCP.
 * Reads configuration from config.properties with environment variable overrides.
 * 
 * Environment variables:
 *   DB_URL      -> overrides db.url
 *   DB_USERNAME -> overrides db.username
 *   DB_PASSWORD -> overrides db.password
 *   DB_POOL_SIZE -> overrides db.pool.size
 */
public class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static volatile HikariDataSource dataSource;

    private DBConnection() {
        // Utility class
    }

    /**
     * Get a connection from the HikariCP pool.
     * Lazily initializes the pool on first call.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            synchronized (DBConnection.class) {
                if (dataSource == null) {
                    initializePool();
                }
            }
        }
        return dataSource.getConnection();
    }

    /**
     * Initialize the HikariCP connection pool.
     */
    private static void initializePool() {
        Properties props = loadProperties();

        String url = getConfigValue("DB_URL", props, "db.url",
                "jdbc:mysql://localhost:3306/ai_coding_mentor?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        String username = getConfigValue("DB_USERNAME", props, "db.username", "root");
        String password = getConfigValue("DB_PASSWORD", props, "db.password", "root");
        int poolSize = Integer.parseInt(
                getConfigValue("DB_POOL_SIZE", props, "db.pool.size", "10"));

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000);       // 5 minutes
        config.setMaxLifetime(600000);       // 10 minutes
        config.setConnectionTimeout(10000);  // 10 seconds
        config.setPoolName("AIMentorPool");

        // MySQL performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        try {
            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP pool initialized successfully: " + url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database pool", e);
            throw new RuntimeException("Cannot initialize database connection pool", e);
        }
    }

    /**
     * Load properties from config.properties on the classpath.
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = DBConnection.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                LOGGER.warning("config.properties not found on classpath; using defaults and env vars");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error reading config.properties", e);
        }
        return props;
    }

    /**
     * Get a configuration value with priority: Environment Variable > Properties File > Default.
     */
    private static String getConfigValue(String envKey, Properties props,
                                          String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) {
            return envVal;
        }
        return props.getProperty(propKey, defaultValue);
    }

    /**
     * Shutdown the connection pool. Call during application shutdown.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("HikariCP pool shut down");
        }
    }
}

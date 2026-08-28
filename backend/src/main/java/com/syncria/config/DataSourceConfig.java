package com.syncria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        
        String url = databaseUrl;
        
        // Handle Render's URL format: postgresql://user:pass@host/db
        // Convert to: jdbc:postgresql://host:5432/db
        if (url != null && !url.startsWith("jdbc:")) {
            // Remove the protocol prefix
            String connectionPart = url.replace("postgresql://", "").replace("postgres://", "");
            
            // Split user:pass@host/db
            String[] parts = connectionPart.split("@", 2);
            if (parts.length == 2) {
                String hostDb = parts[1];
                
                // Check if port is already included
                if (!hostDb.contains(":")) {
                    // Add default port
                    hostDb = hostDb.replace("/", ":5432/");
                    if (!hostDb.contains(":5432/")) {
                        hostDb = hostDb + ":5432";
                    }
                }
                
                url = "jdbc:postgresql://" + hostDb;
            } else {
                url = "jdbc:postgresql://" + connectionPart;
            }
        }
        
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // Connection pool settings
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        return new HikariDataSource(config);
    }
}

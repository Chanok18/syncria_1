package com.syncria.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        String url = databaseUrl.trim();

        // Strip jdbc: prefix if present to get the raw URI
        String raw = url;
        if (raw.startsWith("jdbc:")) {
            raw = raw.substring(5);
        }

        // raw is now like: postgresql://user:pass@host/db or postgresql://host:port/db
        try {
            URI uri = URI.create(raw);
            String host = uri.getHost();
            int port = uri.getPort();
            String database = uri.getPath();
            if (database.startsWith("/")) {
                database = database.substring(1);
            }

            if (port == -1) {
                port = 5432;
            }

            url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

            // Use credentials from URL if present, otherwise from env vars
            String userInfo = uri.getUserInfo();
            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                config.setUsername(parts[0]);
                config.setPassword(parts[1]);
            } else {
                config.setUsername(username);
                config.setPassword(password);
            }
        } catch (Exception e) {
            // If parsing fails, use the URL as-is
            config.setUsername(username);
            config.setPassword(password);
        }

        config.setJdbcUrl(url);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }
}

package com.farming.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    // Railway provides MYSQL_URL in format: mysql://user:password@host:port/database
    @Value("${MYSQL_URL:}")
    private String mysqlUrl;

    @Bean
    public DataSource dataSource() {
        try {
            if (mysqlUrl != null && !mysqlUrl.isBlank() && mysqlUrl.startsWith("mysql://")) {
                URI uri = new URI(mysqlUrl);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 3306 : uri.getPort();
                String database = uri.getPath().replaceFirst("/", "");
                String[] userInfo = uri.getUserInfo().split(":", 2);
                String username = userInfo[0];
                String password = userInfo.length > 1 ? userInfo[1] : "";

                String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true";

                return DataSourceBuilder.create()
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Failed to parse MYSQL_URL, falling back to defaults: " + e.getMessage());
        }

        // Local dev fallback
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/farmingdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true")
                .username("root")
                .password("")
                .build();
    }
}

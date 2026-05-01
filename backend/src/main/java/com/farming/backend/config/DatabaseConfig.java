package com.farming.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Value("${MYSQL_URL:#{null}}")
    private String mysqlUrl;

    @Value("${MYSQLHOST:localhost}")
    private String host;

    @Value("${MYSQLPORT:3306}")
    private String port;

    @Value("${MYSQLDATABASE:farmingdb}")
    private String database;

    @Value("${MYSQLUSER:root}")
    private String username;

    @Value("${MYSQLPASSWORD:}")
    private String password;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        // Try MYSQL_URL first
        if (mysqlUrl != null && !mysqlUrl.trim().isEmpty() && !mysqlUrl.contains("${{")) {
            try {
                URI dbUri = new URI(mysqlUrl);
                String dbUser = username;
                String dbPass = password;
                
                if (dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":", 2);
                    dbUser = userInfo[0];
                    if (userInfo.length > 1) {
                        dbPass = userInfo[1];
                    }
                }

                String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() 
                               + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

                return DataSourceBuilder.create()
                        .url(dbUrl)
                        .username(dbUser)
                        .password(dbPass)
                        .driverClassName("com.mysql.cj.jdbc.Driver")
                        .build();
            } catch (Exception e) {
                // Ignore and fallback to individual variables
            }
        }

        // Fallback to individual variables
        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + database 
                       + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(username)
                .password(password)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}

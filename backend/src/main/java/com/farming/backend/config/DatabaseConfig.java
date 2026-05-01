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

    @Value("${spring.datasource.username:root}")
    private String defaultUsername;

    @Value("${spring.datasource.password:}")
    private String defaultPassword;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        if (mysqlUrl != null && !mysqlUrl.trim().isEmpty()) {
            URI dbUri = new URI(mysqlUrl);
            
            String username = defaultUsername;
            String password = defaultPassword;
            
            if (dbUri.getUserInfo() != null) {
                String[] userInfo = dbUri.getUserInfo().split(":", 2);
                username = userInfo[0];
                if (userInfo.length > 1) {
                    password = userInfo[1];
                }
            }

            // Append allowPublicKeyRetrieval=true and useSSL=false explicitly
            String dbUrl = "jdbc:mysql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath() 
                           + "?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

            return DataSourceBuilder.create()
                    .url(dbUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        }

        // Fallback to local
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/farmingdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC")
                .username(defaultUsername)
                .password(defaultPassword)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}

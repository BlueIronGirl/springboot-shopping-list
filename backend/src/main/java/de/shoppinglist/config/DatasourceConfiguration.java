package de.shoppinglist.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration-Class providing the Datasource-Properties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DatasourceConfiguration {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}

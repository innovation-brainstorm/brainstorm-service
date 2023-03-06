package org.brainstorm.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

//@Configuration
public class DataSourceConfiguration {

    @Autowired
    Environment env;

    public HikariConfig dataSourceConfig() {
        HikariConfig config = new HikariConfig();
        try {
            config.setJdbcUrl(env.getProperty("JDBC_"));
        } catch (Exception e) {

        }

        return null;
    }
}

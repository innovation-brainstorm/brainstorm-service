package org.brainstorm.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

//    @Primary
//    @Bean
//    public DataSource primaryDataSource() {
//        DataBaseConnectionInfoDTO connectionInfo = new DataBaseConnectionInfoDTO();
//
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(connectionInfo.getDriver());
//        dataSource.setUrl(connectionInfo.getUrl());
//        dataSource.setUsername("citi_brainstorm");
//        dataSource.setPassword(connectionInfo.getPassword());
//
//        return dataSource;
//    }

//    @Bean(name = "thirdPartyDataSource")
//    public DataSource mysqlDataSource() {
//        DataBaseConnectionInfoDTO connectionInfo = new DataBaseConnectionInfoDTO();
//
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(connectionInfo.getDriver());
//        dataSource.setUrl(connectionInfo.getUrl());
//        dataSource.setUsername(connectionInfo.getUsername());
//        dataSource.setPassword(connectionInfo.getPassword());
//
//        return dataSource;
//    }
//
//    @Bean(name = "thirdPartyTemplate")
//    public JdbcTemplate primaryJdbcTemplate(@Qualifier("thirdPartyDataSource") DataSource dataSource) {
//        return new JdbcTemplate(dataSource);
//    }


}
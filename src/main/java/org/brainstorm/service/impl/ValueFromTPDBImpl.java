package org.brainstorm.service.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.brainstorm.model.dto.DataBaseConnectionInfoDTO;
import org.brainstorm.service.ValueFromTPDB;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
//@Scope("prototype")
@Slf4j
public class ValueFromTPDBImpl implements ValueFromTPDB {


    private DataSource userDataSource;

    @PostConstruct
    private void createUserDataSource() {
        DataBaseConnectionInfoDTO conInfo = new DataBaseConnectionInfoDTO();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(conInfo.getUrl());
        hikariConfig.setDriverClassName(conInfo.getDriver());
        hikariConfig.setUsername(conInfo.getUsername());
        hikariConfig.setPassword(conInfo.getPassword());
        hikariConfig.setSchema(conInfo.getSchema());
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setAutoCommit(true);
        this.userDataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public List<String> getDataByColumnMySQL(String table, String column, int start, int end) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        try (Statement stat = this.userDataSource.getConnection().createStatement()) {
            ResultSet resultSet = stat.executeQuery(String.format("select %s from %s limit %d,%d", column, table, start, end));
            while (resultSet.next()) {
                String value = resultSet.getString(1);
                res.add("\"" + value + "\"");
            }
        }
        return res;
    }

    @Override
    public List<String> selectDataByStatement(String statement) throws SQLException,RuntimeException {
        ArrayList<String> res = new ArrayList<>();
        Connection conn = this.userDataSource.getConnection();
        try (Statement stat = conn.createStatement()) {
            ResultSet resultSet = stat.executeQuery(statement);
            while (resultSet.next()) {
                String value = resultSet.getString(1);
                res.add("\"" + value + "\"");
            }
        } finally {
            conn.close();
        }
        return res;
    }

    @Override
    public int getColumnsize(String tableName,String column) throws SQLException,RuntimeException {
        Connection conn = this.userDataSource.getConnection();
        try (Statement stat = conn.createStatement()) {
            ResultSet resultSet = stat.executeQuery(String.format("select count(%s) from %s", column,tableName));
            resultSet.next();
            return resultSet.getInt(1);
        } finally {
            conn.close();
        }
    }

    @Override
    public void executeScript(String filePath) throws SQLException {
        FileSystemResource resource = new FileSystemResource(filePath);
        Connection conn = this.userDataSource.getConnection();
        ScriptUtils.executeSqlScript(conn, resource);
        if (!conn.getAutoCommit()) {
            conn.commit();
        };
    }
}

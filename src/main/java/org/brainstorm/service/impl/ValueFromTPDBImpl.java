package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.brainstorm.model.dto.DataBaseConnectionInfoDTO;
import org.brainstorm.service.ValueFromTPDB;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
//@Scope("prototype")
@Slf4j
public class ValueFromTPDBImpl implements ValueFromTPDB {

    private Connection connection;

    public ValueFromTPDBImpl() throws ClassNotFoundException, SQLException {
        DataBaseConnectionInfoDTO conInfo = new DataBaseConnectionInfoDTO();
        Class.forName(conInfo.getDriver());
        connection = DriverManager.getConnection(conInfo.getUrl(), conInfo.getUsername(), conInfo.getPassword());
    }

    @Override
    public List<String> getDataByColumnMySQL(String table, String column, int start, int end) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        try (Statement stat = connection.createStatement()) {
            ResultSet resultSet = stat.executeQuery(String.format("select %s from %s limit %d,%d", column, table, start, end));
            while (resultSet.next()) {
                String value = resultSet.getString(1);
                res.add(value);
            }
        }
        return res;
    }
}

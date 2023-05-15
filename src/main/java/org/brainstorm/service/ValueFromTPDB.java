package org.brainstorm.service;

import java.sql.SQLException;
import java.util.List;

public interface ValueFromTPDB {
    List<String> getDataByColumnMySQL(String table, String column, int start, int end) throws SQLException;

    void executeScript(String filePath);
}
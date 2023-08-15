package org.brainstorm.service;

import java.sql.SQLException;
import java.util.List;

public interface ValueFromTPDB {
    List<String> getDataByColumnMySQL(String table, String column, int start, int end) throws SQLException;
    List<String> selectDataByStatement(String statement) throws SQLException;
    int  getColumnsize(String table,String column)throws SQLException;
    List<String> getColumnNames(String tableName) throws SQLException;
    void executeScript(String filePath) throws SQLException;

}

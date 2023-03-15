package org.brainstorm.service;

import org.brainstorm.datasource.modle.TableInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ExMetaData {
    List<String> getUserTableNames(Connection conn) throws SQLException;

    Map<String, TableInfo> getAll(Connection conn) throws SQLException;
}

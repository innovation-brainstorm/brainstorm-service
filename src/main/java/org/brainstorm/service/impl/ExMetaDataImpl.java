package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.brainstorm.datasource.modle.Column;
import org.brainstorm.datasource.modle.ForeignKey;
import org.brainstorm.datasource.modle.PrimaryKey;
import org.brainstorm.datasource.modle.TableInfo;
import org.brainstorm.service.ExMetaData;
import org.brainstorm.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class ExMetaDataImpl implements ExMetaData {
    @Override
    public List<String> getUserTableNames(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(conn.getCatalog(), null, null, new String[]{"TABLE"});
        List<String> result = new ArrayList<>();
        while (tables.next()) {
            result.add(tables.getString("TABLE_NAME"));
        }
        return result;
    }

    @Override
    public Map<String, TableInfo> getAll(Connection conn) throws SQLException {
        List<String> userTableNames = getUserTableNames(conn);
        Map<String, TableInfo> res=new HashMap<>();
        for (String userTableName : userTableNames) {
            TableInfo tableInfo =new TableInfo();
            tableInfo.setColumnList(getColumnsInfo(conn,userTableName));
            tableInfo.setPkList(getPrimaryKeysInfo(conn,userTableName));
            tableInfo.setFkList(getFKSInfo(conn,userTableName));
            res.put(userTableName,tableInfo);
        }

        return res;
    }

    public List<Column> getColumnsInfo(Connection conn, String tableName) throws SQLException {
        List<Column> columnsList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet columns = metaData.getColumns(conn.getCatalog(), null, tableName, null)) {
            while (columns.next()) {
                Map<String, Object> map = getInfoMap(columns);
                Column column= JsonUtils.convertJsonToJava(map, Column.class);
                columnsList.add(column);
            }
        }
        return columnsList;
    }

    public List<PrimaryKey> getPrimaryKeysInfo(Connection conn, String tableName) throws SQLException {
        List<PrimaryKey> pkList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet primaryKeys = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName)) {
            while (primaryKeys.next()) {
                Map<String, Object> map = getInfoMap(primaryKeys);
                PrimaryKey primaryKey= JsonUtils.convertJsonToJava(map, PrimaryKey.class);
                pkList.add(primaryKey);
            }

        }
        return pkList;
    }

    public List<ForeignKey> getFKSInfo(Connection conn, String tableName) throws SQLException {
        List<ForeignKey> fkList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet fk = metaData.getImportedKeys(conn.getCatalog(), null, tableName)) {
            while (fk.next()) {
                Map<String, Object> map = getInfoMap(fk);
                ForeignKey foreignKey= JsonUtils.convertJsonToJava(map, ForeignKey.class);
                fkList.add(foreignKey);
            }
        }
        return fkList;
    }

    public Map<String,Object> getInfoMap(ResultSet infos) throws SQLException {
        HashMap<String, Object> res = new HashMap<>();
        for (int i = 1; i <= infos.getMetaData().getColumnCount(); i++) {
            String key = infos.getMetaData().getColumnName(i);
            res.put(key, infos.getString(key));
        }
        log.info(res.toString());
        return res;
    }

}

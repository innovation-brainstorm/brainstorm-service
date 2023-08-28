package org.brainstorm.service.impl;

import com.mysql.cj.jdbc.ConnectionImpl;
import lombok.extern.slf4j.Slf4j;
import org.brainstorm.datasource.modle.Column;
import org.brainstorm.datasource.modle.ForeignKey;
import org.brainstorm.datasource.modle.PrimaryKey;
import org.brainstorm.datasource.modle.TableInfo;

import org.brainstorm.interfaces.strategyselect.Strategy;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.ExMetaData;
import org.brainstorm.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class ExMetaDataImpl implements ExMetaData {
    @Value("${AI.CHECK.MODEL.URL}")
    private String AI_CHECK_MODEL_URL;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DataGenerateStrategyService dataGenerateStrategyService;

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
            TableInfo tableInfo = getTableInfo(conn,userTableName);
            res.put(userTableName,tableInfo);
        }

        //check if model already exist for given column
        Properties properties = ((ConnectionImpl) conn).getProperties();
        String host = String.valueOf(properties.get("host"));
        String port = String.valueOf(properties.get("port"));
        String database = String.valueOf(properties.get("dbname"));
        String modelId = host.replace('.', '-') + "-" + port + "-" + database;

        for (String userTableName : userTableNames) {
            TableInfo tableInfo = res.get(userTableName);
            if(tableInfo == null) continue;

            for (Column column : tableInfo.getColumnList()) {
                if(column.getIsAutoincrement()) continue;
                String specificModelId = modelId + "_" + userTableName + "_" + column.getColumnName();
                column.setModelId(specificModelId);
                ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(AI_CHECK_MODEL_URL, Boolean.class, specificModelId);
                if (HttpStatus.OK == responseEntity.getStatusCode()) {
                    Boolean body = responseEntity.getBody();
                    column.setPretrained(body);
                }
            }
        }

        return res;
    }

    public TableInfo getTableInfo(Connection conn,String userTableName) throws SQLException {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setColumnList(getColumnsInfo(conn,userTableName));
        tableInfo.setPkList(getPrimaryKeysInfo(conn,userTableName));
        tableInfo.setFkList(getFKSInfo(conn,userTableName));
        return tableInfo;
    }

    public List<Column> getColumnsInfo(Connection conn, String tableName) throws SQLException {
        List<Column> columnsList = new ArrayList<>();
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet columns = metaData.getColumns(conn.getCatalog(), null, tableName, null)) {
            while (columns.next()) {
                Map<String, Object> map = getInfoMap(columns);
                Column column = JsonUtils.convertJsonToJava(map, Column.class);
                genColumnStrategy(column);
                columnsList.add(column);
            }
        }
        return columnsList;
    }

    public void genColumnStrategy(Column column){

        List<Strategy> strategyList = dataGenerateStrategyService.getAllSupportStrategy();

        Map<Integer, String> strategyMap = new HashMap<>();
        strategyMap.put(0,"AImodel");
        strategyList.forEach(strategy -> {
            Integer id = strategy.getIdentifier();
            String name = strategy.getStrategyName();
            strategyMap.put(id,name);
        });

        column.setStrategies(strategyMap);
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

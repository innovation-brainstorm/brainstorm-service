package org.brainstorm.controller;

import org.brainstorm.datasource.modle.DataSourceInfo;
import org.brainstorm.datasource.modle.TableInfo;
import org.brainstorm.service.DataSourceService;
import org.brainstorm.service.ExMetaData;
import org.brainstorm.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.List;
import java.util.Map;

@RestController
public class DbConfigController {

    @Autowired
    ExMetaData metaData;

    @Autowired
    DataSourceService dataSourceService;


//    postman body raw
//    {
//             "url": "jdbc:mysql://********?serverTimezone=GMT%2B8",
//            "username":"root",
//            "password":"***************"
//    }

    @GetMapping("/jdbc/tableNames")
    public Object getTableNames(@RequestBody DataSourceInfo dbConfig) throws SQLException {
        Connection conn = JdbcUtils.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        return metaData.getUserTableNames(conn);
    }

    @GetMapping("/jdbc/dbDDL")
    public Map<String, TableInfo> getDbDDL(@RequestBody DataSourceInfo dbConfig) throws SQLException {
        Connection conn = JdbcUtils.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
        return metaData.getAll(conn);
    }

    @GetMapping("/jdbc/genData")
    public void genData(@RequestBody DataSourceInfo dbConfig,String tableName){
        dataSourceService.genData(dbConfig,tableName);
    }


}

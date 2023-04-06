package org.brainstorm.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.sql.*;

@Slf4j
public class JdbcUtils {
    public static Connection getConnection(String url, String username, String password) {
        String driver="";
        try {
                driver = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
                if (StringUtils.isBlank(driver)) {
                    throw new RuntimeException("无法从url中获得驱动类");
                }

            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到驱动：" + driver);
        }
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            log.error("获取Jdbc链接失败", e);
            throw new RuntimeException("获取Jdbc链接失败：" + e.getMessage());
        }
    }


    public static void close(Statement statement, Connection connection, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
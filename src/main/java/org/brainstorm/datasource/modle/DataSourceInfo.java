package org.brainstorm.datasource.modle;


import lombok.Data;

@Data
public class DataSourceInfo {

    /**
     * URL
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库类型
     */
    private String type;

    /**
     * 最多返回条数
     */
    private int maxRows = -1;

    /**
     * 驱动类
     */
    private String driverClassName;


}

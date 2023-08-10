package org.brainstorm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseConnectionInfoDTO {
    private String url = "jdbc:mysql://1.117.207.54:3306/other_db_mock?autoReconnect=true";
    private String username = "root";
    private String password = "citi_brainstorm";
    private String schema = "other_db_mock";
    private String table = "student";
    private String driver = "com.mysql.cj.jdbc.Driver";
}

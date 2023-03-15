package org.brainstorm.datasource.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseColumn {
    @JsonProperty("TABLE_CAT")
    private String tableCat;
    @JsonProperty("TABLE_NAME")
    private String tableName;
    @JsonProperty("COLUMN_NAME")
    private String columnName;
}

package org.brainstorm.datasource.modle;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {
    private List<Column> columnList;
    private List<PrimaryKey> pkList;
    private List<ForeignKey> fkList;
}

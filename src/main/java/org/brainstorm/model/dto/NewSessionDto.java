package org.brainstorm.model.dto;

import lombok.Data;
import org.brainstorm.model.MODE;

import java.util.List;

@Data
public class NewSessionDto {
    private String schema;
    private String table;
    private long quantity;
    private MODE destination;
    private List<Column> columns;
}

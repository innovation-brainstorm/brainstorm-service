package org.brainstorm.model.dto;

import lombok.Data;

@Data
public class Column {
    private String name;
    private int strategy;
    private String shell_path;
    private String modelId;
    private Boolean isPretrained;
}

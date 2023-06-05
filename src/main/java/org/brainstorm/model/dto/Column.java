package org.brainstorm.model.dto;

import lombok.Data;

@Data
public class Column {
    private String name;
    private int strategy;
    private String modelId;
    private Boolean isPretrained;
}

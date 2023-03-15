package org.brainstorm.datasource.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PrimaryKey extends BaseColumn{
    @JsonProperty("KEY_SEQ")
    private Integer keySeq;
    @JsonProperty("PK_NAME")
    private String  pkName;
}

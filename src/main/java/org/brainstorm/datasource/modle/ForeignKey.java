package org.brainstorm.datasource.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ForeignKey {
    @JsonProperty("PKTABLE_CAT")
    private String pktableCat;
    @JsonProperty("PKTABLE_NAME")
    private String pktableName;
    @JsonProperty("PKCOLUMN_NAME")
    private String pkcolumnName;


    @JsonProperty("FKTABLE_CAT")
    private String fktableCat;
    @JsonProperty("FKTABLE_NAME")
    private String fktableName;
    @JsonProperty("FKCOLUMN_NAME")
    private String fkcolumnName;

    @JsonProperty("FK_NAME")
    private String fkName;

    @JsonProperty("KEY_SEQ")
    private Integer keySeq;
    @JsonProperty("UPDATE_RULE")
    private Integer updateRule;
    @JsonProperty("DELETE_RULE")
    private Integer deleteRule;

    @JsonProperty("PK_NAME")
    private String pkName;
    @JsonProperty("DEFERRABILITY")
    private Integer deferrability;

}

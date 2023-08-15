package org.brainstorm.datasource.modle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.brainstorm.config.jsonhelper.YesNoDeserializer;

import java.util.List;
import java.util.Map;

@Data
public class Column extends BaseColumn {

   @JsonProperty("TYPE_NAME")
   private String typeName;

   @JsonProperty("COLUMN_SIZE")
   private Integer columnSize;

   @JsonProperty("BUFFER_LENGTH")
   private Integer bufferLength;

   @JsonProperty("IS_NULLABLE")
   @JsonDeserialize(using = YesNoDeserializer.class)
   private Boolean isNullable;

   @JsonProperty("IS_AUTOINCREMENT")
   @JsonDeserialize(using = YesNoDeserializer.class)
   private Boolean isAutoincrement;

   @JsonProperty("IS_GENERATEDCOLUMN")
   @JsonDeserialize(using = YesNoDeserializer.class)
   private Boolean isGeneratedcolumn;

   @JsonProperty("IS_PRETRAINED")
   private boolean pretrained;

   @JsonProperty("MODEL_ID")
   private String modelId;

   private Map<Integer,String> Strategies;
   private String shell_path;

//    DECIMAL_DIGITS:null
//    NUM_PREC_RADIX:10
//    NULLABLE:0
//    REMARKS:
//    SQL_DATA_TYPE:0
//    SQL_DATETIME_SUB:0
//    ORDINAL_POSITION:1

}

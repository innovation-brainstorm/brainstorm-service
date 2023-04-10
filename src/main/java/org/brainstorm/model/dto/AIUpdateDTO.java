package org.brainstorm.model.dto;

import lombok.Data;
import org.brainstorm.instant.Status;

@Data
public class AIUpdateDTO {
    private Long sessionId;
    private Long taskId;
    private String columnName;
    private Long actualCount;
    private Status status;
    private String filePath;

}

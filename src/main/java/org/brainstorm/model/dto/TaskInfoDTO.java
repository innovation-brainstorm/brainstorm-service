package org.brainstorm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brainstorm.instant.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfoDTO {
    private Long sessionId;
    private Long taskId;
    private String columnName;
    private Long expectedCount;
    private Status status;
    private String filePath;
}

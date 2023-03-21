package org.brainstorm.model.dto;

import lombok.Data;
import org.brainstorm.instant.Status;

@Data
public class TaskResponseDto {
    private Long sessionId;
    private Long taskId;
    private Status status;
}

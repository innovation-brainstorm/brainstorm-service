package org.brainstorm.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brainstorm.instant.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    private Long sessionId;
    private Long taskId;
    private Status status;
}

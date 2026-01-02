package com.bbay.mindnote.dto;

import com.bbay.mindnote.entity.TaskPriority;
import com.bbay.mindnote.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDateTime dueDate,
        
        Long categoryId,
        Set<String> tags,
        Long noteId 
) {
    public TaskRequest {
        if (tags == null) tags = Set.of();
    }
}